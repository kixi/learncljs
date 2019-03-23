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
                 :q1 {:id :q1
                      :label "Question 1"
                      :components [:c1 :c2]}
                 :q2 {:id :q2
                      :label "Question 2"
                      :components [:c4 :c3 :c5]}}

    :visibility-rules {:c5 ['= :c4 "yes"]}
    :validations [{:rule [:required :c1] :show [:c1] :error "Field is required"}
                  {:rule [:min-length :c1 3] :show [:c1] :error "At least 3 chars required"}
                  {:rule [:min-length :c2 3] :show [:c2] :error "At least 3 chars required"}
                  {:rule [:required :c5] :show [:c5] :error "Field is required"}
                  ]

    :questions [:q1 :q2]
    :selected-question :q1
    :visibility #{:c1 :c2 :c3 :c4 :c5}
    :values {:c1 "Hello"
             :c2 "World"}
    :temp-values {:c1 ""
                  :c2 ""}}))

(rf/reg-event-db
 ::inc
 (fn [db _]
   (update db :count inc)))

(rf/reg-event-db 
 ::set-text
 (fn [db [_ [component value]]]
   (assoc-in db [:values component] value)))

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
