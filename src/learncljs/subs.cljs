(ns learncljs.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub ::name (fn [db _] (:name db)))
(rf/reg-sub ::count (fn [db _] (:count db)))
(rf/reg-sub ::components (fn [db _] (:components db)))
(rf/reg-sub ::values (fn [db _] (:values db)))
(rf/reg-sub ::temp-values (fn [db _] (:temp-values db)))
(rf/reg-sub ::visibility (fn [db _] (:visibility db)))
(rf/reg-sub ::selected-question (fn [db _] (:selected-question db)))
(rf/reg-sub ::questions (fn [db _] (:questions db)))

(rf/reg-sub
 ::form-components
 (fn [_ _]
   [(rf/subscribe [::selected-question])
    (rf/subscribe [::questions])
    (rf/subscribe [::components])])
 (fn [[selected-question questions components]]
   (let [c-on-page (:components (components selected-question))]
     (map components c-on-page))))

(rf/reg-sub
 ::question-list
 (fn [_ _]
   [(rf/subscribe [::questions])
    (rf/subscribe [::components])])
 (fn [[questions components]]
   (map components questions)))
