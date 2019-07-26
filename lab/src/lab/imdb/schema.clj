(ns lab.imdb.core
  (:require [clojure.repl :refer :all]
            [clojure.pprint :as pp]
            [clojure.java.io :as io]
            [tool.dgraph.core :refer [q create-client 
                                     set-schema 
                                     drop-all 
                                     count-total-nodes]])
  ;
  )


(comment

  (def c (create-client {:with-auth-header? false
                         :hostname          "server"
                         :port              9080}))


  (drop-all c)

  (count-total-nodes c)


; schema
(->
 (q {:qstring "schema(pred: [imdb.genre.name, imdb.title.genres imdb.name.primaryName]) {
  type
  index
}"
     :client  c
     :vars    {}})

 (pp/pprint))
  
  (def schema-string "
              <imdb.title.averageRating>: float @index(float) .
              <imdb.title.numVotes>: int @index(int) .
              
              <imdb.akas.title>: uid .
              <imdb.akas.ordering>: int .
              <imdb.akas.titleString>: string @index(exact,fulltext) @count .
              <imdb.akas.region>: string .
              <imdb.akas.language>: string .
              <imdb.akas.attributes>: [string] .
              <imdb.akas.isOriginalTitle>: bool .
               
              <imdb.title.titleType>: string .
              <imdb.title.primaryTitle>: string @index(fulltext) @count .
              <imdb.title.originalTitle>: string .
              <imdb.title.isAdult>: bool .
              <imdb.title.startYear>: int .
              <imdb.title.endYear>: int .
              <imdb.title.runtimeMinutes>: int .
              <imdb.title.genres>: uid @reverse .
              
              <imdb.title.directors>: uid @reverse .
              <imdb.title.writers>: uid @reverse .
               
              <imdb.episode.parentTconst>: uid .
              <imdb.episode.seasonNumber>: int .
              <imdb.episode.episodeNumber>: int .
               
              <imdb.principals.title>: uid . 
              <imdb.principals.name>: uid .
              <imdb.principals.ordering>: int .
              <imdb.principals.category>: string .
              <imdb.principals.job>: string .
              <imdb.principals.characters>: string .
               
              <imdb.name.primaryName>: string @index(fulltext) @count .
              <imdb.name.birthYear>: int .
              <imdb.name.deathYear>: int .
              <imdb.name.primaryProfession>: [string] @index(term) .
              <imdb.name.knownForTitles>: uid @reverse .
               
              <imdb.genre.name>: string @index(fulltext,term) @count .
               
              ")
  
  (spit "/opt/.data/imdb.rdf/imdb.schema" schema-string)

  (set-schema {:schema-string schema-string
               :client        c})

  (set-schema {:schema-string "
              <imdb.title.averageRating>: float @index(float) .
              <imdb.title.numVotes>: int @index(int) .
              
              <imdb.akas.title>: uid .
              <imdb.akas.ordering>: int @index(int) .
              <imdb.akas.titleString>: string @index(exact,fulltext) @count .
              <imdb.akas.region>: string  @index(term,fulltext) .
              <imdb.akas.language>: string @index(term) .
              <imdb.akas.attributes>: [string] @index(term,fulltext) @count .
              <imdb.akas.isOriginalTitle>: bool @index(bool) .
               
              <imdb.title.titleType>: string @index (term) .
              <imdb.title.primaryTitle>: string .
              <imdb.title.originalTitle>: string .
              <imdb.title.isAdult>: bool @index (bool) .
              <imdb.title.startYear>: int @index(int) .
              <imdb.title.endYear>: int @index(int) .
              <imdb.title.runtimeMinutes>: int @index(int) .
              <imdb.title.genres>: [string] @index (term,fulltext) .
              
              <imdb.title.directors>: uid .
              <imdb.title.writers>: uid .
               
              <imdb.episode.series>: uid .
              <imdb.episode.seasonNumber>: int @index (int) .
              <imdb.episode.episodeNumber>: int @index (int) .
               
              <imdb.principals.title>: uid . 
              <imdb.principals.name>: uid .
              <imdb.principals.ordering>: int @index (int) .
              <imdb.principals.category>: string @index(exact) .
              <imdb.principals.job>: string @index(exact) .
              <imdb.principals.characters>: string @index(exact,fulltext) .
               
              <imdb.name.name>: string @index(exact,fulltext) .
              <imdb.name.primaryName>: string .
              <imdb.name.birthYear>: int  @index(int) .
              <imdb.name.deathYear>: int  @index(int) .
              <imdb.name.primaryProfession>: [string]  @index (term,fulltext) .
              <imdb.name.knownForTitles>: uid .
               
              "
               :client        c})


  ;
  )