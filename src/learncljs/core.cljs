(ns ^:figwheel-hooks learncljs.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [goog.dom :as gdom]
            [learncljs.views :as v]
            [learncljs.events :as e]
            [learncljs.routes :as routes]))

(defn get-app-element []
  (gdom/getElement "app"))


(defn mount []
  (when-let [node (get-app-element)]
    (r/render [v/main-panel] node)))

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
