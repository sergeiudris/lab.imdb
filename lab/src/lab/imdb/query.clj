(ns lab.imdb.query
  (:require [clojure.repl :refer :all]
            [clojure.pprint :as pp]
            [lab.dgraph.core :refer [q create-client set-schema]])
  ;
  )


(comment

  (def c (create-client {:with-auth-header? false
                         :hostname          "server"
                         :port              9080}))



  (as-> nil X
    (identity "{
  all(func: has(imdb.title.primaryTitle)) {
    count(uid)
        }
  }")
    (q {:qstring X
        :client  c
        :vars    {}})

    (pp/pprint X))
  
  (as-> nil X
   (identity "
            query  q($primaryTitle: string) {
             title(func: anyoftext(imdb.title.primaryTitle,$primaryTitle)) {
                 imdb.title.primaryTitle
               }
             }
             ")
   (q {:qstring X
       :client  c
       :vars    {"primaryTitle" "The Matrix" }})

   (pp/pprint X))
  
  
  
  
  
;
  )