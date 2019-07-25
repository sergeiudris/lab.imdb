(ns main
  (:require [tool.nrepl]
            [tool.core]
            [tool.io.core]
            [tool.dgraph.core]
            [tool.pedestal.server]
            [lab.dgraph.core]
            [lab.dgraph.sample]
            [lab.imdb.core]
            [lab.imdb.etl]
            [lab.imdb.query]
            [lab.imdb.psql]
   ;
            )
  ;
  )


(defn -dev  [& args]
  (dev.nrepl/-main)
  (dev.pedestal.server/run-dev)
  )

(defn -main  [& args]
  (dev.nrepl/-main)
  )

