(defproject people "0.1.0-SNAPSHOT"
  :description "Some random data about people"
  :url "https://github.com/xaabi6"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [grafter "0.7.0"]
                 [grafter/vocabularies "0.1.3"]
                 ;;[grafter/vocabularies "0.1.4-SNAPSHOT"]
                 [org.slf4j/slf4j-jdk14 "1.7.5"]]

  :repl-options {:init (set! *print-length* 200)
                 :init-ns people.pipeline }

  :jvm-opts ^:replace ["-server"
                       ;;"-XX:+AggressiveOpts"
                       ;;"-XX:+UseFastAccessorMethods"
                       ;;"-XX:+UseCompressedOops"
                       ;;"-Xmx4g"
                       ]

  :plugins [[lein-grafter "0.7.0"]]
  :min-lein-version "2.5.1"

  )
