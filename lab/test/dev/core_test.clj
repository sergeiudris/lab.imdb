(ns dev.core-test
  (:require [clojure.test :refer :all]
            [clojure.repl :refer :all]
            [dev.core]

   ;
            ))


(deftest parse-int-test
  (is (= (dev.core/parse-int "42") 
      42))
  )


(comment
  
  (clojure.test/run-tests)
  
  ;
  )