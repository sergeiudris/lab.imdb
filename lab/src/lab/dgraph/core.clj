(ns lab.dgraph.core
  (:require [clojure.repl :refer :all]
            [cheshire.core :as json]
            [clojure.pprint :as pp]
            [clojure.reflect :refer :all]

   ;
            )
  (:import (lab.dgraph Example)
           (io.grpc ManagedChannel ManagedChannelBuilder Metadata
                    Metadata$Key)
           (io.grpc.stub MetadataUtils)
           (io.dgraph DgraphClient DgraphGrpc
                      Transaction  DgraphGrpc$DgraphStub
                      DgraphProto$Mutation DgraphProto$Operation
                      DgraphProto$Response)
           (com.google.gson Gson)
           (com.google.protobuf ByteString)
   ;
           )
  ;
  )

(defn create-client
  "create DgraphClient"
  [{:keys [with-auth-header?
           hostname
           port]}]
  (let [ch   (->
              (ManagedChannelBuilder/forAddress hostname port)
              (.usePlaintext true)
              (.build))
        stub (DgraphGrpc/newStub ch)]
    (cond
      with-auth-header? (let [md   (->
                                    (Metadata.)
                                    (.put
                                     (Metadata$Key/of "auth-token" Metadata/ASCII_STRING_MARSHALLER)
                                     "the-auth-token-value"))
                              stub (MetadataUtils/attachHeaders stub md)]
                          (DgraphClient. (into-array [stub])))
      :else (DgraphClient. (into-array [stub]))
      ; :else stub
      
      )))

(defn q-res
  "returns a Response protocol buffer object "
  [{:keys [client
           qstring
           vars]}]
  (let [res (->
             (.newTransaction client)
             (.queryWithVars qstring vars))]
    res))

(defn res->str
  "Returns Response protobuf object to string"
  [res]
  (->
   (.getJson res)
   (.toStringUtf8)))

(defn q
  "Queries Dgraph"
  [opts]
  (->
   (q-res opts)
   (res->str)
   (json/parse-string)
   ))


(defn prn-members
  "Prints unique members of an instance using clojure.reflect"
  [inst]
  (->>
   (reflect inst)
   (:members)
   (sort-by :name)
   (map #(:name %))
   (set)
   (into [])
   (sort)
   pp/pprint
  ;  (pp/print-table )
  ;  (pp/print-table [:name :flags :parameter-types])
   ))



(comment

  Metadata$Key

  (Example/hello)

  (Example/run)

  (Example/prn "asd")


  (Example/main)

  (Example/prn)


  (.println (System/out) "hi")

  (def c (create-client {:with-auth-header? false
                         :hostname          "server"
                         :port              9080}))

  (def qstring (str "query all($a: string){\n"  "all(func: eq(name, $a)) {\n"  "    name\n"  "  }\n"  "}"))

  (->
   (q {:client  c
       :qstring qstring
       :vars    {"$a" "Alice"}})
   json/parse-string)
  
  (def res (->
            (q-res {:client  c
                    :qstring qstring
                    :vars    {"$a" "Alice"}})))
  
  (->>
   (reflect res)
   (:members)
   (sort-by :name)
   (map #(:name %))
   (set)
   (into [])
   (sort)
   pp/pprint
  ;  (pp/print-table )
  ;  (pp/print-table [:name :flags :parameter-types])
   )

  (prn-members res)
  
  (sort ["a" "c" "b"])
  
  (->
   (.getTxn res)
  ;  (.toStringUtf8)
   prn-members
   )
  
  (def qstring2 "query all($a: string) {
     all(func: eq(name, $a)) {
     name
     }
    }")
  
  
  
  (->
   (q {:client  c
       :qstring qstring2
       :vars    {"$a" "Alice"}})
   json/parse-string)
  
  
  ;;;
  )


(defn mutate
  "Transact dgraph mutation"
  [{:keys [data client]}]
  (let [txn (.newTransaction client)]
    (try
      (let [mu  (->
                 (DgraphProto$Mutation/newBuilder)
                 (.setSetJson (ByteString/copyFromUtf8 (json/generate-string data)))
                 (.build))]
        (.mutate txn mu)
        (.commit txn)
        )
      (catch Exception e (str "caught exception: " (.getMessage e)))
      (finally (.discard txn)))
    ;
    ))


(comment
  
  (mutate {:data {"name" "John"}
           :client c
           })
  
  ;;;
  )

(defn set-schema
  "Set the dgraph schema"
  [{:keys [schema-string
           client]}]
  (let [op (->
            (DgraphProto$Operation/newBuilder)
            (.setSchema schema-string)
            (.build))]
    (.alter client op)))

(comment

  (set-schema {:schema-string "
              xid: string @index (exact) .
              "
               :client        c})

  (->
   (q {:qstring "
      {q                 (func:allofterms (name @en, \"Kathryn Bigelow\"))
       {_predicate_ } 
      }
      "
       :client  c
       :vars    {}})
   (pp/pprint))



  (->
   (q {:qstring "{
  caro(func: allofterms(name@en, \"Marc Caro\")) {
    name@en
    director.film {
      name@en
    }
  }
  jeunet(func: allofterms(name@en, \"Jean-Pierre Jeunet\")) {
    name@en
    director.film {
      name@en
    }
  }
}"
       :client  c
       :vars    {}})

   (pp/pprint))


  (->
   (q {:qstring "{
  coactors(func:allofterms(name@en, \"Jane Campion\")) @cascade {
    JC_films as director.film {      # JC_films = all Jane Campion's films
      starting_movie: name@en
      starring {
        JC_actors as performance.actor {      # JC_actors = all actors in all JC films
          actor : name@en
          actor.film {
            performance.film @filter(not uid(JC_films)) {
              film_together : name@en
              starring {
                # find a coactor who has been in some JC film
                performance.actor @filter(uid(JC_actors)) {
                  coactor_name: name@en
                }
              }
            }
          }
        }
      }
    }
  }
}"
       :client  c
       :vars    {}})
   (get-in "data" "coactors")
   (count)
   (pp/pprint))



  (->
   (q {:qstring "{
  PJ as var(func:allofterms(name@en, \"Peter Jackson\")) @normalize @cascade {
    F as director.film
  }

  peterJ(func: uid(PJ)) @normalize @cascade {
    name : name@en
    actor.film {
      performance.film @filter(uid(F)) {
        film_name: name@en
      }
      performance.character {
        character: name@en
      }
    }
  }
}"
       :client  c
       :vars    {}})
   (pp/pprint))


  (->
   (q {:qstring "{
  var(func: allofterms(name@en, \"Taraji Henson\")) {
    actor.film {
      F as performance.film {
        G as genre
      }
    }
  }

  Taraji_films_by_genre(func: uid(G)) {
    genre_name : name@en
    films : ~genre @filter(uid(F)) {
      film_name : name@en
    }
  }
}"
       :client  c
       :vars    {}})
   (get-in "data" "coactors")
   (count)
   (pp/pprint))


  (->
   (q {:qstring "{
  q(func: allofterms(name@en, \"Ang Lee\")) {
    director.film {
      uid
      name@en

      # Count the number of starring edges for each film
      num_actors as count(starring)

      # In this block, num_actors is the value calculated for this film.
      # The film with uid and name
    }

    # Here num_actors is a map of film uid to value for all
    # of Ang Lee's films
    #
    # It can't be used directly, but aggregations like min and max
    # work over all the values in the map

    most_actors : max(val(num_actors))
  }

  # to use num_actors in another query, make sure it's done in a context
  # where the film uid to value map makes sense.
}
"
       :client  c
       :vars    {}})
   (pp/pprint))



  (->
   (q {:qstring "{
  ID as var(func: allofterms(name@en, \"Steven Spielberg\")) {

    # count the actors and save to a variable

    # average as ...
  }

  # average is a map from uid to value so it must be used in a context
  # where the map makes sense.  Because query block avs works over the UID
  # of Steven Spielberg, the value variable has the value we expect.
  avs(func: uid(ID)) @normalize {
    name : name@en
    # get the average
    # also count the movies
  }
}
"
       :client  c
       :vars    {}})
   (pp/pprint))


  (->
   (q {:qstring "
       {
  ID as var(func: allofterms(name@en, \"Steven\")) {
    director.film {
      num_actors as count(starring)
    }
    average as avg(val(num_actors))
  }

  avs(func: uid(ID), orderdesc: val(average)) @filter(ge(val(average), 40)) @normalize {
    name : name@en
    average_actors : val(average)
    num_films : count(director.film)
  }
}

"
       :client  c
       :vars    {}})
   (pp/pprint))



  (->
   (q {:qstring "
    {
	var(func:allofterms(name@en, \"Jean-Pierre Jeunet\")) {
		name@en
		films as director.film {
			stars as count(starring)
			directors as count(~director.film)
			ratio as math(stars / directors)
		}
	}

	best_ratio(func: uid(films), orderdesc: val(ratio)){
		name@en
		stars_per_director : val(ratio)
		num_stars : val(stars)
	}
}


"
       :client  c
       :vars    {}})
   (pp/pprint))


  (->
   (q {:qstring "
 { # Get all directors
  var(func: has(director.film)) @cascade {
    director.film {
      date as initial_release_date
    }
    # Store maxDate as a variable
    maxDate as max(val(date))
    daysSince as math(since(maxDate)/(24*60*60))
  }

  # Order by maxDate
  me(func: uid(maxDate), orderdesc: val(maxDate), first: 10) {
    name@en
    days : val(daysSince)

    # For each director, sort by release date and get latest movie.
    director.film(orderdesc: initial_release_date, first: 1) {
      name@en
      initial_release_date
    }
  }
}


"
       :client  c
       :vars    {}})
   (pp/pprint))



  (->
   (q {:qstring "
{
  var(func:allofterms(name@en, \"Steven Spielberg\")) {
    director.film @groupby(genre) {
      a as count(uid)
    }
  }

  byGenre(func: uid(a), orderdesc: val(a)) {
    name@en
    num_movies : val(a)
  }
}

"
       :client  c
       :vars    {}})
   (pp/pprint))


  (->
   (q {:qstring "
{
  var(func:allofterms(name@en, \"Cherie Nowlan\")) {
    pred as _predicate_
  }

  q(func:allofterms(name@en, \"Cherie\")) {
    expand(val(pred)) { expand(_all_) }
  }
}


"
       :client  c
       :vars    {}})
   (pp/pprint))
  
  
  (->
   (q {:qstring "
{movie             (func:alloftext (name @de, \"Die schwarz\"))
 @filter    (has (genre))
 {name @de
  name @en
  name @it} }

"
       :client  c
       :vars    {}})
   (pp/pprint))



  ;;;
  )

