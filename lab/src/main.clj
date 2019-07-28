(ns main
  (:require [tool.nrepl]
            [tool.core]
            [tool.io.core]
            [tool.dgraph.core]
            [tool.pedestal.server]
            [lab.dgraph]
            [lab.imdb.schema]
            [lab.imdb.etl]
            [lab.imdb.query]
            [lab.imdb.psql]
            [lab.stack.psql]
   ;
            )
  ;
  )


(defn -dev  [& args]
  (tool.nrepl/-main)
  (tool.pedestal.server/run-dev)
  )

(defn -main  [& args]
  (tool.nrepl/-main)
  )

