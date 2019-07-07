(ns learncljs.cards
  (:require
   [devcards.core :as dc]
                                        ; Load namespaces with `defcard` or `deftest` definitions
   [learncljs.core])
  (:require-macros
   [devcards.core :refer [defcard]]))

(devcards.core/start-devcard-ui!)

(defcard first-card
  (dc/reagent [:h1 "hello"]))
