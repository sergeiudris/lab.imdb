(ns main
  (:require [dev.nrepl]
            [dev.core]
            [dev.pedestal.server]
            [lab.dgraph.core]
            [lab.dgraph.sample]
            [lab.imdb.core]
            [lab.imdb.etl]
            [lab.imdb.query]
   ;
            )
  ;
  )


(defn -dev  [& args]
  (dev.nrepl/-main)
  (dev.pedestal.server/run-dev)
  )

(defn -main  [& args]
  (dev.nrepl/-main)
  )

(comment
  
  ; (Examples/hello)
  {:hello "world"}

  (System/getProperty "java.vm.version")
  (System/getProperty "java.version")
  (System/getProperty "java.specification.version")
  (clojure-version)

  
  ;
  )