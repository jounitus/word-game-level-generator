(defproject generator "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {
    :uberjar {:aot :all}}
  :dependencies [
    [org.clojure/clojure "1.8.0"]
    [http-kit "2.3.0"]
    [cheshire "5.8.1"]
    [compojure "1.6.1"]
    ]
  :main generator.server
  )
