(ns dev.core
  (:require [clojure.repl :refer :all]
            ;
            ))


(defn parse-int [number-string]
  (try (Integer/parseInt number-string)
       (catch Exception e nil)))


(comment


  (parse-int "3")

  ;
  )