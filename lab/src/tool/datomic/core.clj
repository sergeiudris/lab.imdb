(ns tool.datomic.core
  (:require [clojure.repl :refer :all]
            [datomic.api :as d]
            [clojure.pprint :as pp]))


(defn get-paginted-entity
  "Returns entities and total count given limit,offset and attribute keword"
  [{:keys [attribute limit offset]
    :or   {limit  10
           offset 0}}]
  {:entities (->>
              (d/q '{:find  [?e (count ?e)]
                     :in    [$ ?attribute]
                     :where [[?e ?attribute]]}
                   (cdb) attribute)
              (drop offset)
              (take limit)
              (map  #(identity (d/pull (cdb) '[*] (first %))))
              (into []))
   :count    (d/q '{:find  [(count ?e) .]
                    :in    [$ ?attribute]
                    :where [[?e ?attribute]]}
                  (cdb) attribute)})

