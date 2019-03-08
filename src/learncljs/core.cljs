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
                  :label "Label1:"}
                 {:id :c2
                  :label "Label2"}]
    :values {:c1 "Hello"
             :c2 "World"}
    :temp-values {:c1 "Hello"
                  :c2 "World"}}))


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

(rf/reg-sub
 :name
 (fn [db _]
   (:name db)))

(rf/reg-sub
 :count
 (fn [db _]
   (:count db)))

(rf/reg-sub
 :components
 (fn [db _]
   (:components db)))

(rf/reg-sub
 :values
 (fn [db _]
   (:values db)))

(rf/reg-sub
 :temp-values
 (fn [db _]
   (:temp-values db)))

(def style-label {:font-weight 700})
(def style-descr {:font-weight 400
                  :color "#757575"})
(def style-input {:height "2rem"
                  :font-size "16px"
                  :border "0.0625rem solid rgba(0,0,0,.4)"
                  :padding ".666rem 1rem"
                  :border-radius ".1875rem"
                  :line-height "1.33"
                  :color "rgba(0,0,0,.8)"})

             

(defn input [{:keys [id value label description error on-save on-change]}]
  (let [save #(on-save (-> value str clojure.string/trim))
        change on-change] 
    [:label 
     [:div {:style {:margin "1rem 0"}}
      [:div {:style style-label} label]
      (when description [:div {:style style-descr} description])]
     [:input {:style style-input
              :type "text"
              :value value
              :on-change #(change (-> % .-target .-value))
              :on-blur save}]]))

(defn model []
  [:div (str @(rf/subscribe [:values]))])


(defn form []
  (let [components @(rf/subscribe [:components])
        values @(rf/subscribe [:temp-values])]
    (println "RRender form" values)
    [:div 
     (for [c components]
       ^{:key (:id c)}
       [:div
        [input {:value (values (:id c))
                :label (:label c)
                :description "Descr"
                :id (:id c)
                :on-save #(rf/dispatch [:set-text [(:id c) %]])
                :on-change #(rf/dispatch [:set-text-temp [(:id c) %]])}]])
     [model]]))

(defn main-panel []
  [:div 
   [:div (str "Here I am !!! " @(rf/subscribe [:count]))]
   [:input {:type "button"
            :value "inc"
            :on-click #(rf/dispatch [:inc])}]
   [form]])

(defn mount []
  (when-let [node (get-app-element)]
    (r/render [main-panel] node)))

(defn ^:before-load my-before-reload-callback []
  (println "BEFORE reload!!!"))

(defn ^:after-load my-after-reload-callback []
  (mount))

(defn ^:export init []
  (rf/dispatch-sync [:init-db])
  (let [node (.getElementById js/document "app")]
    (r/render [main-panel] node)))

;;(init)

;;
;; (rf/dispatch [:init-db])
;;

