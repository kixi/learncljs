(ns ^:figwheel-hooks learncljs.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [goog.dom :as gdom]))

(js/console.log "Hello World!")

(defn get-app-element []
  (gdom/getElement "app"))

(rf/reg-event-db
 :init-db
 (fn [db _]
   {:name "Version"
    :count 0
    :components [{:id :c1
                  :type :input
                  :label "Vorname"}
                 {:id :c2
                  :type :input
                  :label "Nachname"}]
    :visibility #{:c1}
    :values {:c1 "Hello"
             :c2 "World"}
    :temp-values {:c1 ""
                  :c2 ""}}))


(rf/reg-event-db
 :inc
 (fn [db _]
   (update db :count inc)))

(rf/reg-event-db
 :set-text
 (fn [db [_ [component value]]]
   (assoc-in db [:values component] value)))

(rf/reg-event-db
 :set-text-temp
 (fn [db [_ [component value]]]
   (assoc-in db [:temp-values component] value)))

(rf/reg-event-db
 :show
 (fn [db [_ component]]
   (update db :visibility conj component)))

(rf/reg-event-db
 :hide
 (fn [db [_ component]]
   (update db :visibility disj component)))

(rf/reg-sub :name (fn [db _] (:name db)))
(rf/reg-sub :count (fn [db _] (:count db)))
(rf/reg-sub :components (fn [db _] (:components db)))
(rf/reg-sub :values (fn [db _] (:values db)))
(rf/reg-sub :temp-values (fn [db _] (:temp-values db)))
(rf/reg-sub :visibility (fn [db _] (:visibility db)))

(def style-label {:font-weight 700})
(def style-descr {:font-weight 400
                  :color "#757575"})
(def style-input {:height "2rem"
                  :font-size "16px"
                  :border "0.0625rem solid rgba(0,0,0,.2)"
                  :padding ".366rem 1rem"
                  :border-radius ".1875rem"
                  :line-height "1.33"
                  :color "rgba(0,0,0,.8)"})

             

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
  (let [components @(rf/subscribe [:components])
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
                :on-save #(rf/dispatch [:set-text [(:id c) %]])
                :on-change #(rf/dispatch [:set-text-temp [(:id c) %]])}]])
     [model]]))


(def style-sidebar {:padding "1rem"
                    :float "left"
                    :min-height "100vh"})
(defn sidebar []
  [:div {:style style-sidebar}
   [:ul
    [:li "item 1"]
    [:li "item 2"]]])



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
  (rf/dispatch-sync [:init-db])
  (mount))

;;(init)

;;
;; (rf/dispatch [:init-db])
;; (rf/dispatch [:show :c2])
;; (rf/dispatch [:hide :c2])
