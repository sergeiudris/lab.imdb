(ns lab.imdb.psql
  (:require [clojure.repl :refer :all]
            [clojure.pprint :as pp]
            [lab.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as cstr]
            [clojure.java.jdbc :as jdbc]
            [dev.core :refer [prn-members]]
            [clj-time.core :as ctime]
            [clj-time.format :as ctimef]
            [clj-time.jdbc]
   ;
            )
  ;
  )


(def pgdb-spec
  {:dbtype   "postgresql"
   :dbname   "pgsqldb"
   :user     "pgsql"
   :host     "pgsql"
   :port     5432
   :password "pgsql"})

(comment

  (jdbc/query pgdb-spec ["
                         
    CREATE TABLE account(
                         user_id serial PRIMARY KEY,
                              username VARCHAR (50) UNIQUE NOT NULL,
                              password VARCHAR (50) NOT NULL,
                              email VARCHAR (355) UNIQUE NOT NULL,
                              created_on TIMESTAMP NOT NULL,
                              last_login TIMESTAMP
                         );
                         "])

  (jdbc/query pgdb-spec ["select * from account"])

  (.getTime (java.util.Date.))
  (prn-members (java.util.Date.))
  (str (java.util.Date.))
  (.toGMTString (java.util.Date.))

  (java.sql.Timestamp/valueOf "2004-10-19 10:23:54")
  (type (java.sql.Timestamp/valueOf "2004-10-19 10:23:54"))
  (java.sql.Timestamp/valueOf (.toString (java.util.Date.)))

  (ctime/now)

  (jdbc/insert! pgdb-spec "account" {"username"   "leo"
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