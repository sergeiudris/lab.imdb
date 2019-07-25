(ns tool.io.core
  (:require [clojure.repl :refer :all]
            [clojure.reflect :refer :all]
            [clojure.pprint :as pp]
            [clojure.java.io :as io]
            [clojure.string :as cstr]
            ;
            ))

(defn read-nth-line
  "Read line-number from the given text file. The first line has the number 1."
  [filename line-number]
  (with-open [rdr (clojure.java.io/reader filename)]
    (nth (line-seq rdr) (dec line-number))))

(defn count-lines
  [filename]
  (with-open [rdr (clojure.java.io/reader filename)]
    (count (line-seq rdr))))

(defn write-lines
  "Write lines vector to file"
  [filename lines-vec]
  (with-open [w (clojure.java.io/writer filename :append true)]
    (doseq [line lines-vec]
      (.write w (nl line)))))

(defn delete-files
  [& filenames]
  (doseq [filename filenames]
    (.delete (java.io.File. filename))))

(defn mk-dirs
  "Make directories in the path"
  [path]
  (.mkdirs (java.io.File. path)))

(defn create-file
  [filename]
  (.createNewFile (java.io.File. filename-title-rating-rdf)))



(comment
  ;
  )