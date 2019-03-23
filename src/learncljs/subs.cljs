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

(defn eval-rule [[op field v0] values]
  (let [val (get-in values [field])]
    (= val v0)))

(defn log-intercept [x]
  (println "log...." x)
  x)

(rf/reg-sub
 ::visibility
 (fn [_ _]
   [(rf/subscribe [::values])
    (rf/subscribe [::visibility-rules])
    (rf/subscribe [::components])])
 (fn [[values visibility-rules components]]
   (let [rules-for (into #{} (keys visibility-rules))
         all-comps (into #{} (keys components))
         always-visible (clojure.set/difference all-comps rules-for)
         cond-visible (->> visibility-rules
                           (map (fn [[k rule]] [k (eval-rule rule values)]))
                           (filter (fn [[k visible]]
                                     visible))
                           (map first)
                           (into #{}))]
     (clojure.set/union always-visible cond-visible))))

(rf/reg-sub
 ::form-components
 (fn [_ _]
   [(rf/subscribe [::selected-question])
    (rf/subscribe [::components])
    (rf/subscribe [::visibility])])
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
   [(rf/subscribe [::validation-errors])])
 (fn [[validation-errors]]
   (->> validation-errors
        log-intercept
        (map (fn [[k v]] [k "Validation Error"]))
        (into {})
        log-intercept)
   ))