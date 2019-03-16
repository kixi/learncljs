(ns ^:figwheel-hooks learncljs.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [goog.dom :as gdom]
            [learncljs.events :as e]
            [learncljs.routes :as routes]))

(defn get-app-element []
  (gdom/getElement "app"))


(rf/reg-sub :name (fn [db _] (:name db)))
(rf/reg-sub :count (fn [db _] (:count db)))
(rf/reg-sub :components (fn [db _] (:components db)))
(rf/reg-sub :values (fn [db _] (:values db)))
(rf/reg-sub :temp-values (fn [db _] (:temp-values db)))
(rf/reg-sub :visibility (fn [db _] (:visibility db)))
(rf/reg-sub :selected-question (fn [db _] (:selected-question db)))
(rf/reg-sub :questions (fn [db _] (:questions db)))

(rf/reg-sub
 :form-components
 (fn [_ _]
   [(rf/subscribe [:selected-question])
    (rf/subscribe [:questions])
    (rf/subscribe [:components])])
 (fn [[selected-question questions components]]
   (let [c-on-page (:components (components selected-question))]
     (map components c-on-page))))

(rf/reg-sub
 :question-list
 (fn [_ _]
   [(rf/subscribe [:questions])
    (rf/subscribe [:components])])
 (fn [[questions components]]
   (map components questions)))

(def style-label {:font-weight 700})
(def style-descr {:font-weight 400
                  :color "#757575"})
(def style-input {:height "2rem"
                  :font-size "16px"
                  :border "0.0625rem solid rgba(0,0,0,.2)"
                  :padding ".366rem 1rem"
                  :border-radius ".1875rem"
                  :line-height "1.33"
                  :color "rgba(0,0,0,.8)"
                  :background-color "white"})

(defn input [{:keys [id value label description error on-save on-change]}]
  [:label
   [:div {:style {:margin "1rem 0"}}
    [:div {:style style-label} label]
    (when description [:div {:style style-descr} description])]
   [:input {:style style-input
            :type "text"
            :value value
            :on-change #(on-change (-> % .-target .-value))
            :on-blur #(on-save (-> % .-target .-value))}]])

(defn model []
  [:div (str @(rf/subscribe [:values]))])

(defn form []
  (let [components @(rf/subscribe [:form-components])
        values @(rf/subscribe [:temp-values])
        visibility @(rf/subscribe [:visibility])]
    [:div {:style {:padding "1rem"}}
     (for [c components
           :when (visibility (:id c))]
       ^{:key (:id c)}
       [:div
        [input {:value (values (:id c))
                :label (:label c)
                :description "Descr"
                :id (:id c)
                :on-save #(rf/dispatch [::e/set-text [(:id c) %]])
                :on-change #(rf/dispatch [::e/set-text-temp [(:id c) %]])}]])
     [model]]))

(def style-sidebar {:padding "1rem"
                    :float "left"
                    :min-height "100vh"})
(defn sidebar []

  [:div {:style style-sidebar}
   [:ul
    (for [q @(rf/subscribe [:question-list])]
      ^{:key (:id q)}
      [:li [:a {:href (str "/#/question/" (name (:id q)))} (:label q)]])]])

(defn frame [header sidebar content footer]
  [:div
   [:div header]
   [:div {:style {:display "flex"}} sidebar content]
   [:div footer]])

(defn main-panel []
  [frame
   [:div] [sidebar] [form] [:div]])

(defn mount []
  (when-let [node (get-app-element)]
    (r/render [main-panel] node)))

(defn ^:before-load my-before-reload-callback []
  (println "BEFORE reload!!!"))

(defn ^:after-load my-after-reload-callback []
  (mount))

(defn ^:export init []
  (rf/dispatch-sync [::e/init-db])
  (routes/app-routes)
  (mount))

;;(init)

;;
;; (rf/dispatch [::e/init-db])
;; (rf/dispatch [::e/show :c2])
(rf/dispatch [::e/select-question :q1])
;; (rf/dispatch [:hide :c2])
