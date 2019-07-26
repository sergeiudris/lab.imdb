(ns lab.imdb.psql
  (:require [clojure.repl :refer :all]
            [clojure.pprint :as pp]
            [lab.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as cstr]
            [clojure.java.jdbc :as jdbc]
            [tool.core :refer [prn-members]]
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



(comment

  (def file-dir "/opt/app/.data/imdb/")

  (def file-names {:titles  "title.basics.tsv"
                   :names   "name.basics.tsv"
                   :crew    "title.crew.tsv"
                   :ratings "title.ratings.tsv"})

  (def files (reduce-kv (fn [acc k v]
                          (assoc acc k (str files-dir v))) {} file-names))

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
                              runtimeMinutes INT,
                              genres VARCHAR (512)
                         );
                         "])
  (jdbc/execute! db ["
                         
    CREATE TABLE names(
                              nconst VARCHAR(50) PRIMARY KEY NOT NULL, 
                              primaryName  VARCHAR (512),
                              birthYear INT,
                              deathYear INT,
                              primaryProfession VARCHAR (512),
                              knownForTitles VARCHAR (256)
                         );
                         "])



  (jdbc/execute! db ["
                         
    CREATE TABLE crew(
                              id SERIAL PRIMARY KEY,
                              tconst VARCHAR (50) NOT NULL,
                              directors TEXT,
                              writers TEXT
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



  (jdbc/execute! db ["
                         
    CREATE TABLE name_titles(
                              id SERIAL PRIMARY KEY,
                              nconst VARCHAR(50) NOT NULL, 
                              tconst VARCHAR (50 ) NOT NULL
                         );
                         "])


  (jdbc/execute! db ["
                         
    CREATE TABLE title_directors(
                              id SERIAL PRIMARY KEY,
                              nconst VARCHAR(50) NOT NULL, 
                              tconst VARCHAR (50 ) NOT NULL
                         );
                         "])

  (jdbc/execute! db ["
                         
    CREATE TABLE title_writers(
                              id SERIAL PRIMARY KEY,
                              nconst VARCHAR(50) NOT NULL, 
                              tconst VARCHAR (50 ) NOT NULL
                         );
                         "])


;
  )
  


  
