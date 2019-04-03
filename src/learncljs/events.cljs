(ns learncljs.events
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
 ::init-db
 (fn [db _]
   {:name "Version"
    :count 0
    :components {:c1 {:id :c1
                      :type :text
                      :label "Vorname"}
                 :c2 {:id :c2
                      :type :text
                      :label "Nachname"}
                 :c3 {:id :c3
                      :type :text
                      :label "WTF"}
                 :c4 {:id :c4
                      :type :radio
                      :options ["yes" "no"]
                      :label "Please select"}
                 :c5 {:id :c5
                      :type :text
                      :label "Label 5"}
                 :e1 {:id :e1
                      :type :edit-grid
                      :components [:ce1 :ce2]}
                 :ce1 {:id :ce1
                       :type :text
                       :label "CE1"}
                 :ce2 {:id :ce2
                       :type :text
                       :label "CE2"}
                 :q1 {:id :q1
                      :label "Question 1"
                      :components [:c1 :c2 :e1]}
                 :q2 {:id :q2
                      :label "Question 2"
                      :components [:c4 :c3 :c5]}}

    :visibility-rules {:c5 ['= [0 :c4] "yes"]}
    :validations [{:rule [:required [0 :c1]] :show [[0 :c1]] :error "Field is required"}
                  {:rule [:min-length :c1 3] :show [:c1] :error "At least 3 chars required"}
                  {:rule [:min-length :c2 3] :show [:c2] :error "At least 3 chars required"}
                  {:rule [:required :c5] :show [:c5] :error "Field is required"}
                  ]

    :questions [:q1 :q2]
    :touched #{}
    :selected-question :q1
    :visibilities  #{:c1 :c2 :c3 :c4 :e1 :ce1 :ce2}
    :values {[0 :c1] "Hello"
             [0 :c2] "World"
             :e1 0}
    :temp-values {:c1 "Hello"
                  :c2 "World"
                  :e1 [{:ce1 ""} {}]}}))

(rf/reg-event-db
 ::add-grid-row
 (fn [db [_ grid-component-id]]
   (update-in db [:values grid-component-id] inc)))

(rf/reg-event-db
 ::inc
 (fn [db _]
   (update db :count inc)))

(rf/reg-event-fx 
 ::set-text
 (fn [{:keys [db]} [_ [component value]]]
   {:db (-> db
            (assoc-in [:values component] value)
            (update-in [:touched] conj component))
    :dispatch-n [[::update-visibilities] [::validate]]}))


(rf/reg-event-db
 ::set-text-temp
 (fn [db [_ [component value]]]
   (assoc-in db [:temp-values component] value)))

(rf/reg-event-db
 ::show
 (fn [db [_ component]]
   (update db :visibility conj component)))

(rf/reg-event-db
 ::hide
 (fn [db [_ component]]
   (update db :visibility disj component)))

(rf/reg-event-db
 ::select-question
 (fn [db [_ question]]
   (assoc db :selected-question question)))

(defn eval-validation-rule [{:keys [rule] :as validation} values]
  (let [[r id & params] rule
        v (values id)]
    (println rule v r)
    (condp = r
      :required (and v (> (.-length (.trim v)) 0))
      :min-length (if v (>= (.-length (.trim v)) 3) true)
      true)))

(eval-validation-rule {:rule [:required :c1] :show [:c1] :error "Field is required"} {:c1 "   "})
(eval-validation-rule {:rule [:min-length :c1 3] :show [:c1] :error "Field is required"} {:c2 "224"})

(rf/reg-event-db
 ::validate
 (fn [db [_ _]]
   (let [values (:values db)
         validations (:validations db)]
     (assoc db :validation-errors
            (->> validations
                 (filter #(not (eval-validation-rule % values)))
                 (group-by (fn [rule] (first (:show rule))))
                 )))))


(defn eval-rule [[op field v0] values]
  (let [val (get-in values [field])]
    (= val v0)))

(rf/reg-event-db
 ::update-visibilities
 (fn [db [_ _]]
   (let [values (:values db)
         visibility-rules (:visibility-rules db)
         components (:components db)]
     (let [rules-for (into #{} (keys visibility-rules))
           all-comps (into #{} (keys components))
           always-visible (clojure.set/difference all-comps rules-for)
           cond-visible (->> visibility-rules
                             (map (fn [[k rule]] [k (eval-rule rule values)]))
                             (filter (fn [[k visible]]
                                       visible))
                             (map first)
                             (into #{}))]
       (assoc-in db [:visibilities] (clojure.set/union always-visible cond-visible))))))

(rf/reg-event-fx
 ::save-letter
 (fn [cofx [_ _]]
   {:db (assoc (:db cofx) :persistance :saving)}))

(:validations @re-frame.db/app-db)
(:validation-errors @re-frame.db/app-db)
(:values @re-frame.db/app-db)
re-frame.db/app-db
