(ns lab.stack.core
  (:require [clojure.repl :refer :all]
            [clojure.pprint :as pp]
            [clojure.java.io :as io]
            [clojure.string :as cstr]
            [clojure.java.jdbc :as jdbc]
            [tool.core :refer [prn-members nth-seq split-tab drop-nth]]
            [tool.io.core :refer [delete-files create-file
                                  read-nth-line count-lines mk-dirs]]
            [clj-time.core :as ctime]
            [clj-time.format :as ctimef]
            [clj-time.jdbc])
  )

(def db
  {:dbtype   "postgresql"
   :dbname   "postgresdb"
   :user     "postgres"
   :host     "postgres-stack"
   :port     5432
   :password "postgres"})

(defn pqry
  [db query-vec]
  (time (->
         (jdbc/query db query-vec)
         (pp/pprint))))

(def filedir "/opt/.data/stack/")



(comment
  
  (pqry db ["select * from x"])
  
  (read-nth-line (str filedir "Badges.xml" ) 1000 )
  
  (read-nth-line (str filedir "Comments.xml") 1000)
  
  (read-nth-line (str filedir "PostHistory.xml") 1000)
  (read-nth-line (str filedir "PostLinks.xml") 1000)
  
  (read-nth-line (str filedir "Posts.xml") 10)
  
  (read-nth-line (str filedir "Tags.xml") 10)
  
  (read-nth-line (str filedir "Users.xml") 10)
  
  (read-nth-line (str filedir "Votes.xml") 10)
  
  
  
  
  
  ;
  )