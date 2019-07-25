(ns dev.core
  (:require [clojure.repl :refer :all]
            [clojure.reflect :refer :all]
            [clojure.pprint :as pp]
            ;
            ))


(defn parse-int [number-string]
  (try (Integer/parseInt number-string)
       (catch Exception e nil)))


(defn prn-members
  "Prints unique members of an instance using clojure.reflect"
  [inst]
  (->>
   (reflect inst)
   (:members)
   (sort-by :name)
   (map #(:name %))
   (set)
   (into [])
   (sort)
   pp/pprint
  ;  (pp/print-table )
  ;  (pp/print-table [:name :flags :parameter-types])
   ))

(comment


  (parse-int "3")

  ;
  )