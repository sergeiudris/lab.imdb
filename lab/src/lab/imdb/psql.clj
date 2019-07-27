(ns lab.imdb.psql
  (:require [clojure.repl :refer :all]
            [clojure.pprint :as pp]
            [lab.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as cstr]
            [clojure.java.jdbc :as jdbc]
            [tool.core :refer [prn-members nth-seq split-tab]]
            [tool.io.core :refer [delete-files create-file
                                  read-nth-line count-lines mk-dirs]]
            [clj-time.core :as ctime]
            [clj-time.format :as ctimef]
            [clj-time.jdbc]
   ;
            )
  ;
  )


(def db
  {:dbtype   "postgresql"
   :dbname   "postgresdb"
   :user     "postgres"
   :host     "postgres"
   :port     5432
   :password "postgres"})

(comment

  (jdbc/execute! db ["
                         
    CREATE TABLE account(
                         user_id serial PRIMARY KEY,
                              username VARCHAR (50) UNIQUE NOT NULL,
                              password VARCHAR (50) NOT NULL,
                              email VARCHAR (355) UNIQUE NOT NULL,
                              created_on TIMESTAMP NOT NULL,
                              last_login TIMESTAMP
                         );
                         "])

  (jdbc/query db ["select * from account"])

  
  (jdbc/execute! db ["DROP TABLE account"])
  
  
  (.getTime (java.util.Date.))
  (prn-members (java.util.Date.))
  (str (java.util.Date.))
  (.toGMTString (java.util.Date.))

  (java.sql.Timestamp/valueOf "2004-10-19 10:23:54")
  (type (java.sql.Timestamp/valueOf "2004-10-19 10:23:54"))
  (java.sql.Timestamp/valueOf (.toString (java.util.Date.)))

  (ctime/now)

  (jdbc/insert! db "account" {"username"   "leo"
                                     "password"   "root"
                                     "email"      "vinci@gmail.com"
                                     "created_on" (ctime/now)
                                     "last_login" (ctime/now)})

  (.toGMTString (java.util.Date.))
  (.toString (java.util.Date.))
  (.toLocaleString (java.util.Date.))
  (.ttb (java.util.Date.))


  (ctimef/show-formatters)

  (ctime/default-time-zone)

  (def formatter-mysql (f/formatters :mysql))
  (ctimef/parse formatter-mysql (.toString (java.util.Date.)))
  (def multi-parser (f/formatter (t/default-time-zone)  :mysql "YYYY-MM-dd"))

  (f/unparse multi-parser (f/parse multi-parser "2012-02-01"))

  ;
  )

(comment 
  (jdbc/execute! db ["DROP TABLE titles"])
  (jdbc/execute! db ["DROP TABLE names"])
  (jdbc/execute! db ["DROP TABLE crew"])
  (jdbc/execute! db ["DROP TABLE ratings"])
  
  (jdbc/execute! db ["
                     DROP SEQUENCE table_id_seq
                     "])
  
  (jdbc/query db ["select * from titles"])
  
  (->>
   (jdbc/query db ["select * from titles offset 5 limit 5"])
   pp/pprint
   )
  
  
  
  ;
  )

(def filedir "/opt/.data/imdb/")

(def filenames {:titles  "title.basics.tsv"
                 :names   "name.basics.tsv"
                 :crew    "title.crew.tsv"
                 :ratings "title.ratings.tsv"
                 :akas "title.akas.tsv"
                 :principals "title.principals.tsv"
                 :episode "title.episode.tsv"
                })

(def filedir-out "/opt/.data/imdb.out/")
(def filenames-out {:crew "title.crew.out.tsv"})

(def files (reduce-kv (fn [acc k v]
                        (assoc acc k (str filedir v))) {} filenames))
(def files-out (reduce-kv (fn [acc k v]
                        (assoc acc k (str filedir-out v))) {} filenames-out))

(defn process-file!
  "Processes an in-file line by line in a lazy manner
   and writes to out-file"
  [filename-in
   filename-out
   ctx
   line->lines
   & {:keys [limit offset]
      :or   {offset 0}}]
  (with-open [rdr (io/reader filename-in)
              wtr (clojure.java.io/writer filename-out :append true)]
    (let [data        (line-seq rdr)
          header-line (first data)
          header      (cstr/split header-line #"\t")
          ; attrs       (rest header)
          lines       (if limit (take limit (drop offset (rest data))) (rest data))]
      (doseq [line lines]
        (as-> line e
          (do (prn e) e)
          (line->lines e header ctx )
          (cstr/join e \newline )
          (str e \newline)
          (.write wtr e)
          ;
          )))))

; (defn files->rdfs
;   [filenames filename-out specs & {:keys [limits limit]}]
;   (let [ctx (create-ctx nil specs)]
;     (time
;      (do
;        (doseq [src filenames]
;          (names->rdf-3  src filename-out ctx specs :limit (or (get limits src) limit)))
;        (genres->rdf  filename-out  specs ctx)))))

(comment
  (mk-dirs filedir-out)
  
  (process-file! (:crew files) (:crew files-out)  {}
                (fn [line header ctx]
                  (let [vals      (cstr/split line #"\t")
                        title     (nth-seq vals 0)
                        directors (cstr/split (nth-seq vals 1) #",")
                        writers   (cstr/split (nth-seq vals 2) #",")]
                    (concat
                     ()
                     )
                    
                    )
                  )
                 :offset 2000  :limit 10)
  
  (source nth)
  
  (count-lines (:akas files))
  (split-tab (read-nth-line (:akas files) 2500000))
  
  (split-tab (read-nth-line (:episode files) 10000))
  
  ;
  )

(defn drop-tables
  [db tables]
  (doseq [name tables]
    (jdbc/execute! db [(str "DROP TABLE IF EXISTS " name)])))

(comment
  (drop-tables db ["titles" "names" "ratings"
                   "akas" "episodes" "director_credits"
                   "writer_credits" "known_for_titles"
                   "title_genres" "akas_types"])

  (drop-tables db ["crew"])

  ;
  )

(comment

  (jdbc/execute! db ["                     
                     CREATE SEQUENCE table_id_seq
                     "])

  (jdbc/execute! db ["
    CREATE TABLE titles(
                              tconst VARCHAR(50) PRIMARY KEY NOT NULL, 
                              titleType VARCHAR(50),
                              primaryTitle  VARCHAR (512),
                              originalTitle VARCHAR (512),
                              isAdult INT,
                              startYear INT,
                              endYear INT,
                              runtimeMinutes INT
                              -- genres [string]
                         );
                         "])
  (jdbc/execute! db ["
    CREATE TABLE names(
                              nconst VARCHAR(50) PRIMARY KEY NOT NULL, 
                              primaryName  VARCHAR (512),
                              birthYear INT,
                              deathYear INT,
                              primaryProfession VARCHAR (512)
                              -- knownForTitles VARCHAR (256)
                         );
                         "])

  (jdbc/execute! db ["
    CREATE TABLE ratings(
                              id    SERIAL PRIMARY KEY,
                              tconst VARCHAR (50) NOT NULL,
                              averageRating FLOAT8,
                              numVotes  INT
                         );
                         "])

  (jdbc/execute! db ["
    CREATE TABLE akas(
                              tconst  VARCHAR (50),
                              ordering  INT,
                              title TEXT ,
                              region VARCHAR (50),
                              language VARCHAR (50),
                              -- types 
                              -- attributes [string| number]
                              isOriginalTitle INT,
                              PRIMARY KEY (tconst, ordering)
                         );
                         "])

  (jdbc/execute! db ["
    CREATE TABLE episodes(
                              tconst  VARCHAR (50) PRIMARY KEY NOT NULL,
                              parentTconst VARCHAR (50),
                              seasonNumber INT,
                              episodeNumber INT
                         );
                         "])


  (jdbc/execute! db ["
    CREATE TABLE director_credits(
                              nconst VARCHAR(50) NOT NULL, 
                              tconst VARCHAR (50 ) NOT NULL,
                              PRIMARY KEY (nconst, tconst)
                         );
                         "])

  (jdbc/execute! db ["
    CREATE TABLE writer_credits(
                              nconst VARCHAR(50) NOT NULL, 
                              tconst VARCHAR (50 ) NOT NULL,
                              PRIMARY KEY (nconst, tconst)
                         );
                         "])

  (jdbc/execute! db ["
    CREATE TABLE known_for_titles(
                              nconst VARCHAR(50) NOT NULL, 
                              tconst VARCHAR (50 ) NOT NULL,
                              PRIMARY KEY (nconst, tconst)
                         );
                         "])

  (jdbc/execute! db ["
    CREATE TABLE title_genres(
                              name TEXT, 
                              tconst VARCHAR (50),
                              PRIMARY KEY (name, tconst)
                         );
                         "])

  (jdbc/execute! db ["
    CREATE TABLE akas_types(
                              name TEXT, 
                              tconst VARCHAR (50),
                              PRIMARY KEY (name, tconst)
                         );
                         "])


  ;
  )
  

(comment

  (jdbc/execute! db [(str "
                     COPY titles FROM "
                          "'" (:titles files) "'"
                          " DELIMITER E'\t' 
          NULL '\\N'  QUOTE E'\b' ESCAPE E'\b' CSV HEADER 
                     ")])
  ; 6018811

  (jdbc/execute! db [(str "
                     COPY names FROM "
                          "'" (:names files) "'"
                          " DELIMITER E'\t' 
          NULL '\\N'  QUOTE E'\b' ESCAPE E'\b' CSV HEADER 
                     ")])
  ; 9459600

  (jdbc/execute! db [(str "
                     COPY crew(tconst,directors,writers) FROM "
                          "'" (:crew files) "'"
                          " DELIMITER E'\t' 
          NULL '\\N'  QUOTE E'\b' ESCAPE E'\b' CSV HEADER 
                     ")])
  ; 6018811

  (jdbc/execute! db [(str "
                     COPY ratings(tconst,averageRating,numVotes) FROM "
                          "'" (:ratings files) "'"
                          " DELIMITER E'\t' 
          NULL '\\N'  QUOTE E'\b' ESCAPE E'\b' CSV HEADER 
                     ")])
  ; 954349
  

  ;
  )
  
