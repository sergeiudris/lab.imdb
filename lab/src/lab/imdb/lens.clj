(ns lab.imdb.lens
  (:require [clojure.repl :refer :all]
            [clojure.pprint :as pp]
            [clojure.java.jdbc :as jdbc]
            [tool.core :refer [prn-members nth-seq split-tab drop-nth]]
            [tool.io.core :refer [delete-files create-file
                                  read-nth-line count-lines mk-dirs]]
            [clojure.java.io :as io]
            [clojure.string :as cstr]
   ;
            )
  ;
  )

(def file-dir "/opt/.data/lens/ml-latest/")

(def filenames {:genome-scores "genome-scores.csv"
                :genome-tags   "genome-tags.csv"
                :links         "links.csv"
                :movies        "movies.csv"
                :ratings       "ratings.csv"
                :tags          "tags.csv"})

(def files (reduce-kv (fn [acc k v]
                        (assoc acc k (str file-dir v))) {} filenames))

(comment
  
  (source dir)
  (dir tool.core)
  
  (read-nth-line (:genome-scores files ) 1)
  (read-nth-line (:genome-tags files) 1)
  
  (read-nth-line (:movies files) 1)
  (read-nth-line (:tags files) 1)
  (read-nth-line (:links files) 1)
  
  
  
  
  ;
  )