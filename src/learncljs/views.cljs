(ns learncljs.views
  (:require [re-frame.core :as rf]
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
       (let [option-style  {:style style-input
                            :name id
                            :type "radio"
                            :value o
                            :checked (= value o)
                            :on-change #(on-click (-> % .-target .-value))}]
         ^{:key o}
         [:div
          [:input option-style] o]))]))

(defmethod component :text [{:keys [id value label description error on-save on-change]}]
  [:label
   [:div {:style {:margin "1rem 0"}}
    [:div {:style style-label} label]
    (when description [:div {:style style-descr} description])
    (when error [:div {:style style-error} error])]
   [:input {:style style-input
            :type "text"
            :value value
            :on-change #(on-change (-> % .-target .-value))
            :on-blur #(on-save (-> % .-target .-value))}]])

(defn model []
  [:div
   [:div (str @(rf/subscribe [::s/values]))]
   [:div (str @(rf/subscribe [::s/validation-errors]))]])

(defn form []
  (let [components @(rf/subscribe [::s/form-components])
        values @(rf/subscribe [::s/temp-values])
        error-messages @(rf/subscribe [::s/validation-error-messages])]
    [:div {:style {:padding "1rem"}}
     (for [c components]
       ^{:key (:id c)}
       [:div

        [component
         {:type (:type c)
          :options (:options c)
          :value (values (:id c))
          :label (:label c)
          :error (error-messages (:id c))
          :description "Descr"
          :id (:id c)
          :on-save #(rf/dispatch [::e/set-text [(:id c) %]])
          :on-change #(rf/dispatch [::e/set-text-temp [(:id c) %]])}]])
     [model]]))

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
