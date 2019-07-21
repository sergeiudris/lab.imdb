(ns lab.imdb.etl
  (:require [clojure.repl :refer :all]
            [clojure.pprint :as pp]
            [lab.dgraph.core :refer [q create-client set-schema]]
   ;
            )
  ;
  )


(comment

  (def c (create-client {:with-auth-header? false
                         :hostname          "server"
                         :port              9080}))


  (->
   (q {:qstring "{
  caro(func: allofterms(name@en, \"Marc Caro\")) {
    name@en
    director.film {
      name@en
    }
  }
  jeunet(func: allofterms(name@en, \"Jean-Pierre Jeunet\")) {
    name@en
    director.film {
      name@en
    }
  }
}"
       :client  c
       :vars    {}})

   (pp/pprint))

  ;
  )