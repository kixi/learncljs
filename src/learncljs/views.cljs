(ns learncljs.views
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [learncljs.events :as e]
            [learncljs.subs :as s]))

(def style-label {:font-weight 700})
(def style-descr {:font-weight 400
                  :color "#757575"})
(def style-error {:font-weight 700
                  :color "#750000"})
(def style-input {:height "2rem"
                  :font-size "16px"
                  :border "0.0625rem solid rgba(0,0,0,.2)"
                  :padding ".366rem 1rem"
                  :border-radius ".1875rem"
                  :line-height "1.33"
                  :color "rgba(0,0,0,.8)"
                  :background-color "white"})

(defmulti component (fn [{:keys [type]}] type))

(defmethod component :radio [{:keys [id options value label description error on-save on-change]}]
  (letfn [(on-click [opt-val]
            (on-save opt-val)
            (on-change opt-val)) ]
    [:label
     [:div {:style {:margin "1rem 0"}}
      [:div {:style style-label} label]
      (when description [:div {:style style-descr} description])]
     (for [o options]
       (let [option-style  {:style {}
                            :name id
                            :type "radio"
                            :value o
                            :checked (= value o)
                            :on-change #(on-click (-> % .-target .-value))}]
         ^{:key o}
         [:div
          [:input option-style] [:span {:style {:padding "0.5rem"}} o]]))]))

(defmethod component :text [{:keys [id value label description error on-save on-change]}]
  (println label value "=> rendering input component level 1")
  (let [local-value (reagent/atom value)
        typing (reagent/atom false)]
    (fn [{:keys [id value label description error on-save on-change]}]
      (println label value local-value "=> => rendering input component level 2")
      [:label
       [:div {:style {:margin "1rem 0"}}
        [:div {:style style-label} label]
        (when description [:div {:style style-descr} description])
        (when error [:div {:style style-error} error])]
       [:input {:style style-input
                :on-focus #(do 
                               (reset! typing true))
                :type "text"
                :value (if @typing @local-value value)
                :on-change #(reset! local-value (-> % .-target .-value))
                :on-blur #(do (on-save (-> % .-target .-value))
                              (reset! typing false))}]])))

(defn model []
  [:div
   [:div (str @(rf/subscribe [::s/values]))]
   [:div (str @(rf/subscribe [::s/validation-errors]))]])

(declare panel)

(defn edit-grid-row-expanded [components values [idx row]]
  [panel components values {} idx]
  )

(defn edit-grid-row-closed [components values [idx row]]
  (println "edit grid row" values)

  [:div (apply str (map (fn [{:keys [id]}] (str (values [idx id]) " ")) components))]
  )

(defn edit-grid-row [components values row-id]
  (let [expanded (reagent/atom false)]
    (fn [components values row-id]
      [:div
       [:button {:on-click #(swap! expanded not)} "v"]
       (if @expanded
         [edit-grid-row-expanded components values row-id]
         [edit-grid-row-closed components values row-id])])))

(defn edit-grid [c grid-values values]
  (let [components @(rf/subscribe [::s/edit-grid-components (:id c)])]
    (println "EDIT-GRID" grid-values)
    [:div 
     (for [r (map-indexed vector grid-values)]
       [edit-grid-row components values r])]
    ))

(defn dyn-create-component [c values error-messages idx]
  [:div
   (if (= (:type c) :edit-grid)
     [edit-grid c (values (:id c)) values]
     [component
      {:type (:type c)
       :options (:options c)
       :value (values [idx (:id c)])
       :label (:label c)
       :error (error-messages [idx (:id c)])
       :description "Descr"
       :id (:id c)
       :on-save #(rf/dispatch [::e/set-text [[idx (:id c)] %]])
       :on-change #(rf/dispatch [::e/set-text-temp [[idx (:id c)] %]])}])])

(defn panel [components values error-messages idx]
  (println "PANEL" error-messages)
  [:div {:style {:padding "1rem"}}
   (for [c components]
     ^{:key (:id c)}
     [dyn-create-component c values error-messages idx])
   #_[model]]
  )

(defn form []
  (let [components @(rf/subscribe [::s/form-components])
        values @(rf/subscribe [::s/values])
        error-messages @(rf/subscribe [::s/validation-error-messages])]
    [panel components values error-messages 0]))

(defn top-bar []
  [:div
   [:button  {:on-click #(rf/dispatch [::e/save-letter])
              :style {:float "right"}} "save"]])

(def style-sidebar {:padding "1rem"
                    :float "left"
                    :min-width "12rem"
                    :min-height "100vh"})
(defn sidebar []
  (let [sel @(rf/subscribe [::s/selected-question])]
    [:div {:style style-sidebar}
     [:ul
      (for [q @(rf/subscribe [::s/question-list])]
        ^{:key (:id q)}
        [:li [:a {:href (str "/#/question/" (name (:id q))) 
                  :style {:font-weight (if (= sel (:id q)) "bold" "normal")}} (:label q)]])]]))

(defn frame [header sidebar content footer]
  [:div
   [:div header]
   [:div {:style {:display "flex"}} sidebar content]
   [:div footer]])

(defn main-panel []
  [frame
   [top-bar] [sidebar] [form] [:div]])
