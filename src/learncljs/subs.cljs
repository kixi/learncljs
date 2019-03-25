(ns learncljs.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub ::name (fn [db _] (:name db)))
(rf/reg-sub ::count (fn [db _] (:count db)))
(rf/reg-sub ::components (fn [db _] (:components db)))
(rf/reg-sub ::values (fn [db _] (:values db)))
(rf/reg-sub ::temp-values (fn [db _] (:temp-values db)))
(rf/reg-sub ::selected-question (fn [db _] (:selected-question db)))
(rf/reg-sub ::questions (fn [db _] (:questions db)))
(rf/reg-sub ::visibility-rules (fn [db _] (:visibility-rules db)))
(rf/reg-sub ::validations (fn [db _] (:validations db)))
(rf/reg-sub ::validation-errors (fn [db _] (:validation-errors db)))
(rf/reg-sub ::touched (fn [db _] (:touched db)))
(rf/reg-sub ::visibilities (fn [db _] (:visibilities db)))


(defn log-intercept [x]
  (println "log...." x)
  x)


(rf/reg-sub
 ::form-components
 (fn [_ _]
   [(rf/subscribe [::selected-question])
    (rf/subscribe [::components])
    (rf/subscribe [::visibilities])])
 (fn [[selected-question components visibilities]]
   (let [c-on-page (:components (components selected-question))]
     (->> c-on-page
          (filter visibilities)
          (map components)))))

(rf/reg-sub
 ::question-list
 (fn [_ _]
   [(rf/subscribe [::questions])
    (rf/subscribe [::components])])
 (fn [[questions components]]
   (map components questions)))


(rf/reg-sub
 ::validation-error-messages
 (fn [_ _]
   [(rf/subscribe [::validation-errors])
    (rf/subscribe [::touched])])
 (fn [[validation-errors touched]]
   (->> validation-errors
        (map (fn [[k v]] [k "Validation Error"]))
        (filter (fn [[k v]] (touched k)))
        (into {})
        )
   ))
