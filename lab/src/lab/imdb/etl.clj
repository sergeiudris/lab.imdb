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


(def filename-names "/opt/.data/imdb/name.basics.tsv")
(def filename-names-rdf "/opt/.data/imdb.rdf/name.basics.rdf")


(def filename-titles "/opt/.data/imdb/title.basics.tsv")
(def filename-titles-rdf "/opt/.data/imdb.rdf/title.basics.rdf")

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

(defn apply-imdb-specs-val
  "Returns string. Apply specs to val"
  [attr val specs]
  (str  val (get-in specs [:suffix attr])))

(defn apply-imdb-specs-attr
  "Returns string. Apply specs to attr"
  [attr  specs]
  (str (:domain specs) (get-in specs [:subdomains attr]) attr))

(defn tsv-vals->rdf-vec
  "Returns rdf vec"
  [id attrs vals specs]
  (->>
   (map (fn [val attr]
          (if
           (= val "\\N") nil ;(do (prn val) nil)
           (vector (wrap-brackets id)
                   (wrap-brackets (apply-imdb-specs-attr attr specs))
                   (apply-imdb-specs-val attr (wrap-quotes val) specs)
                   "."))) vals attrs)
   (keep #(if (nil? %) nil % ))
   )
  )


(defn tsv-line->rdf-line
  [line attrs specs]
  (let [xs   (split-tab line)
        id   (first xs)
        vals (rest xs)]
    (->>
     (tsv-vals->rdf-vec id attrs vals specs)
     (map #(cstr/join " " %))
                  ;
     )))

(defn tsv-strings->rdf-strings
  "Converts tsv strings to rdf strings"
  [tsv-strings specs]
  (let [header (split-tab (ffirst tsv-strings))
        attrs  (rest header)]
    (map (fn [s]
           (tsv-line->rdf-line (first s) attrs specs)
           ;
           ) (rest tsv-strings))
    ;
    ))

(defn read-nth-line
  "Read line-number from the given text file. The first line has the number 1."
  [file line-number]
  (with-open [rdr (clojure.java.io/reader file)]
    (nth (line-seq rdr) (dec line-number))))

(defn count-lines
  [file]
  (with-open [rdr (clojure.java.io/reader file)]
    (count (line-seq rdr) )))

(comment
  
  (read-nth-line filename-names 1)
  
  
  (read-nth-line filename-names-rdf 500000)
  
  (read-nth-line filename-names-rdf 4)
  
  
  (count-lines filename-names)
  (* 9459601 5)
  (* 9459601 4)
  
  (count-lines filename-names-rdf)
  
  (read-nth-line filename-names-rdf 5000000)
  
  
  ;
  )






(def imdb-specs
  {:domain "imdb."
   :suffix {"averageRating" "^^<xs:float>"
            "numVotes"      "^^<xs:int>"
            "birthYear"      "^^<xs:int>"
            "deathYear"      "^^<xs:int>"
            "name"          "@en"}
   :subdomains {"averageRating" "title."
                "numVotes"      "title."
                "primaryName" "name."
                "birthYear" "name."
                "deathYear" "name."
                "primaryProfession" "name."
                "knownForTitles" "name."
                }
   }
  )

(defn in-steps
  "Process lazy seq in steps"
  [fnc & {:keys [data step total]
          :or   {step 100000}}]
  (let [total*  (or total (count data))
        ran    (range 0 total* step)
        points (concat ran [total*])]
    (prn points)
    (doseq [p points]
      (fnc p ))))

(defn names->rdf
  [filename-in filename-out]
  (with-open [reader (io/reader filename-in)]
    (let [data (csv/read-csv reader)
          step 100000]
      (in-steps
       (fn [p]
         (as-> nil e
           (take step (drop p data))
           (tsv-strings->rdf-strings e imdb-specs)
           (flatten e)
           (write-lines filename-out e)
                    ;
           ))
       :step step
       :data data
      ;  :total 250000
       ;
       ))))

(defn titles->rdf
  [filename-in filename-out]
  (with-open [reader (io/reader filename-in)]
    (let [data (csv/read-csv reader :separator \tab :quote \space )
          step 100000]
      (in-steps
       (fn [p]
         (as-> nil e
           (take step (drop p data))
           (tsv-strings->rdf-strings e imdb-specs)
           (flatten e)
           (write-lines filename-out e)
                    ;
           ))
       :step step
       :data data
       :total 250000
       ;
       ))))


(defn names->rdf-2
  [filename-in filename-out]
  (with-open [reader (io/reader filename-in)
              writer (clojure.java.io/writer filename-out :append true)]
    (let [data        (csv/read-csv reader)
          ; header-line (read-nth-line filename-in 1)
          header-line (ffirst data)
          header      (split-tab header-line)
          attrs       (rest header)]
      ; (prn (count data))
      (doseq [line (take 100 (rest data))]
        ; (prn (tsv-line->rdf-line (first line) attrs imdb-specs))
        (as-> nil e
          (tsv-line->rdf-line (first line) attrs imdb-specs)
          (cstr/join \newline e)
          (str e "\n")
          ; (prn e)
          (.write writer e)
          ;
          )))))



(comment

  (def title-ratings (read-csv-file  filename-title-ratings))

  (names->rdf  filename-names filename-names-rdf)
  
  (names->rdf-2  filename-names filename-names-rdf)
  
  
  (titles->rdf filename-titles filename-titles-rdf)

  (.mkdirs (java.io.File. "/opt/.data/imdb.rdf"))

  (.delete (java.io.File. filename-title-rating-rdf))

  (.delete (java.io.File. filename-names-rdf))

  (.delete (java.io.File. filename-titles-rdf))
  

  (.createNewFile (java.io.File. filename-title-rating-rdf))

  (spit filename-title-rating-rdf (nl "<123> <name> \"asd\" .") :append true)

  (count title-ratings)

  (type title-ratings)

  (take 10 title-ratings)



  (cstr/split "tt0000002\t6.3\t185" #"\t")

  (cstr/join  "3" ["asd" "d"])


  (->
   (tsv-strings->rdf-strings  (take 10 title-ratings) imdb-specs)
   flatten
   pp/pprint)

  (def title-ratings-rdfs (tsv-strings->rdf-strings title-ratings imdb-specs))

  ; (count title-ratings-rdfs)


  (->>
   (drop 500000 title-ratings-rdfs)
   (take 10)
   pp/pprint)

  (->>
   (drop 500000 title-ratings-rdfs)
   (take 100000)
   flatten
   (write-lines filename-title-rating-rdf))

  (let [total  (count title-ratings)
        step   100000
        ran    (range 0 total step)
        points (concat ran [total])]
    (prn points)
    (doseq [p points]
      (->>
       (take step (drop p title-ratings-rdfs))
       flatten
       (write-lines filename-title-rating-rdf))))




  ;
  )


(comment
  
  
  (def mother-of-all-files
    (with-open [rdr (clojure.io/reader "/home/user/.../big_file.txt")]
      (into []
            (comp (partition-by #(= % "")) ;; splits on empty lines (double \n)
                  (remove #(= % "")) ;; remove empty lines
                  (map #(clojure.string/join "\n" %)) ;; group lines together
                  (map clojure.string/trim))
            (line-seq rdr))))
  
  ;
  )