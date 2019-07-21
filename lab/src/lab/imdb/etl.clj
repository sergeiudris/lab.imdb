(ns lab.imdb.etl
  (:require [clojure.repl :refer :all]
            [clojure.pprint :as pp]
            [lab.dgraph.core :refer [q create-client set-schema]]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as cstr]

   ;
            )
  ;
  )

(defn read-column [filename column-index]
  (with-open [reader (io/reader filename)]
    (let [data (csv/read-csv reader)]
      ; (map #(nth % column-index) data) ; lazy
      (mapv #(nth % column-index) data))))

(defn read-csv-file
  [filename]
  (with-open [reader (io/reader filename)]
    (let [data (doall (csv/read-csv reader))]
      data)))

(defn write-file []
  (with-open [w (clojure.java.io/writer  "f:/w.txt" :append true)]
    (.write w (str "hello" "world"))))

(def filename-title-ratings "/opt/.data/imdb/title.ratings.tsv")
(def filename-title-rating-rdf "/opt/.data/imdb.rdf/title.ratings.rdf")


(defn nl
  "append newline char to str"
  [s]
  (str s "\n"))

(defn write-lines
  "Write lines vector to file"
  [filename lines-vec]
  (with-open [w (clojure.java.io/writer filename :append true)]
    (doseq [line lines-vec]
      (.write w (nl line)))))

(defn wrap-brackets
  "Wrap string in brackets"
  [s]
  (str "<" s ">"))

(defn wrap-quotes
  "Wrap string in quotes"
  [s]
  (str "\"" s "\""))

(defn split-tab
  "Splits the string by tab char"
  [s]
  (cstr/split s #"\t"))

(defn tsv-vals->rdf-vec
  "Returns rdf vec"
  [id attrs vals]
  (map(fn [attr val]
                 (vector (wrap-brackets id)
                         (wrap-brackets attr)
                         (wrap-quotes val))) vals attrs))

(defn tsv-strings->rdf-strings
  "Converts tsv strings to rdf strings"
  [tsv-strings]
  (let [header (split-tab (ffirst tsv-strings))
        attrs  (rest header)]
    (map (fn [s]
             (let [xs   (split-tab (first s))
                   id   (first xs)
                   vals (rest xs)]
               (->>
                (tsv-vals->rdf-vec id attrs vals)
                (map #(cstr/join #" " %))
                  ;
                )))(rest tsv-strings))
    ;
    ))


(comment
  
  (def title-ratings (read-csv-file  filename-title-ratings))
  
  (.mkdirs (java.io.File. "/opt/.data/imdb.rdf"))
  
  (.delete (java.io.File. filename-title-rating-rdf))
  
  (.createNewFile (java.io.File. filename-title-rating-rdf))
  
  (spit filename-title-rating-rdf (nl "<123> <name> \"asd\" .") :append true )
  
  (count title-ratings)
  
  (type title-ratings)
  
  (take 10 title-ratings)
  
  ()
  
  (doall )
  
  
  (cstr/split "tt0000002\t6.3\t185" #"\t" )
  
  (cstr/join  "3" ["asd" "d"] )
  
  
  (->
   (tsv-strings->rdf-strings  (take 10 title-ratings))
  ;  flatten
   pp/pprint
   )
  
  (def title-ratings-rdfs (tsv-strings->rdf-strings title-ratings) )
  
  (->>
   (drop 500000 title-ratings-rdfs)
   (take 10 )
   pp/pprint
   )
  
  
  ;
  )