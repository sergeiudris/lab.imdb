(defproject lab "0.1.0"

  :repositories [["conjars" {:url "https://conjars.org/repo"}]
                 ["clojars" {:url "https://clojars.org/repo"}]
                 ["oracle" {:url "https://download.oracle.com/maven"}]



                 ["staging" {:url       "https://repository.apache.org/content/repositories/staging"
                             :snapshots true
                             :update    :always}]
                 ["snapshots" {:url       "https://repository.apache.org/content/repositories/snapshots"
                               :snapshots true
                               :update    :always}]]

  :min-lein-version "2.0.0"

  :plugins [[cider/cider-nrepl "0.21.1"]
            [com.jakemccrary/lein-test-refresh "0.24.1"]
            ; [lein-virgil "0.1.9"]
            ; [mvxcvi/whidbey "2.1.1"]
            ]
  :dependencies [;; casaclog
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/core.async "0.4.490"]
                 [nrepl "0.6.0"]
                 [cider/cider-nrepl "0.21.1"]
                ;  [mvxcvi/puget "1.1.2"]
                 [mvxcvi/whidbey "2.1.1"]
                 [org.clojure/data.csv "0.1.4"]
                 [commons-io/commons-io "2.4"]
                 [clj-http "3.10.0"]
                 [cheshire "5.8.1"]
                 [org.clojure/data.csv "0.1.4"]

                 [io.dgraph/dgraph4j "1.7.3"]
                ;  [com.google.code.gson/gson "2.2.2"]
                ;  [com.google.protobuf/protobuf-java "3.9.0-rc-1"]
                ;  [io.grpc/grpc-all "1.22.1"]
                ;  [io.grpc/grpc-protobuf "1.22.1"]

                 [io.pedestal/pedestal.service       "0.5.7"]

                 [io.pedestal/pedestal.jetty         "0.5.7"]
                 [org.clojure/tools.namespace "0.2.11"]

                 [org.clojure/java.jdbc "0.7.9"]
                 [org.postgresql/postgresql "42.2.6"]
                 [clj-time "0.15.0"]
                ;  [org.postgresql/postgresql "42.2.5.jre7"]

                 ;
                 ]

  :repl-options {:init-ns          main
                 :main             main
                 :host             "0.0.0.0"
                 :port             4001
                 :nrepl-middleware [cider.nrepl/wrap-apropos
                                    cider.nrepl/wrap-classpath
                                    cider.nrepl/wrap-complete
                                    cider.nrepl/wrap-debug
                                    cider.nrepl/wrap-format
                                    cider.nrepl/wrap-info
                                    cider.nrepl/wrap-inspect
                                    cider.nrepl/wrap-macroexpand
                                    cider.nrepl/wrap-ns
                                    cider.nrepl/wrap-spec
                                    cider.nrepl/wrap-profile
                                    cider.nrepl/wrap-refresh
                                    cider.nrepl/wrap-resource
                                    cider.nrepl/wrap-stacktrace
                                    cider.nrepl/wrap-test
                                    cider.nrepl/wrap-trace
                                    cider.nrepl/wrap-out
                                    cider.nrepl/wrap-undef
                                    nrepl.middleware.print/wrap-print
                                    cider.nrepl/wrap-version]
                ;  :nrepl-middleware [cider.piggieback/wrap-cljs-repl]
                 }
  :profiles {:dev  {:main         ^{:skip-aot true}  main
                    :aot          nil ;[dev ]
                    :aliases      {"dev" ["trampoline" "run" "-m" "main/-dev"]}
                    :dependencies [[io.pedestal/pedestal.service-tools "0.5.7"] ;; Only needed for ns-watching; WAR tooling
                                   ]}

             :prod ^:leaky {:main main
                                ;  :uberjar-name "wordcount-standalone.jar"
                                ;  :jar-name     "wordcount.jar"
                            :aot  [main]}}


  :main ^{:skip-aot true} main
  :jvm-opts ["-Xms768m" "-Xmx2048m" "-Xmx1g"]
  ; :javac-opts ["-target" "1.8" "-source" "1.8"]

  ; :whidbey {:print-color     true
  ;           :map-delimiter   ""
  ;           :extend-notation true
  ;           :width           180
  ;           ; :print-meta      true
  ;           }


  :source-paths ["src"]
  :java-source-paths ["src"]  ; Java source is stored separately.
  :test-paths ["test"]
  :resource-paths ["resources"]

  :auto-clean false)
