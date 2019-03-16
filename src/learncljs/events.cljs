(ns learncljs.events
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
 ::init-db
 (fn [db _]
   {:name "Version"
    :count 0
    :components {:c1 {:id :c1
                      :type :input
                      :label "Vorname"}
                 :c2 {:id :c2
                      :type :input
                      :label "Nachname"}
                 :c3 {:id :c3
                      :type :input
                      :label "WTF"}
                 :q1 {:id :q1
                      :label "Question 1"
                      :components [:c1 :c2]}
                 :q2 {:id :q2
                      :label "Question 2"
                      :components [:c3]}}

    :questions [:q1 :q2]
    :selected-question :q1
    :visibility #{:c1 :c2 :c3}
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
