(ns generator.server
  (:gen-class)
  (:require
    [org.httpkit.server :refer :all]
    [generator.core :refer :all]
    [generator.words :refer :all]
    [generator.level :refer :all]
    [cheshire.core :refer :all]
    [compojure.core :refer :all]
    [compojure.route :as route]
    )
  )

(defn split-all-words
  [words]
  (map split-word words)
)

(defn get-server-short-code-and-words-list
  [filename]

  (->>
       (read-lines-from-file filename)
       (filter test-great-word)
       (get-longer-short-code-and-words-list 5)
       (filter (fn [[short-code words]] (>= (count words) 5)))
       (map (fn [[short-code words]] [short-code (split-all-words words)]))
       )

)

(defn generate-level
  [req short-code-and-words-list]
  {
    :status  200
    :headers {"Content-Type" "application/json"}
    :body    (->> (rand-nth short-code-and-words-list)
                  (second)
                  (shuffle)
                  (try-build-level)
                  (clean-level)
                  (generate-string))
  }
)

(defn -main [& args]
  (println "Reading words...")
  (let
    [
      filename "./resources/google-10000-english-no-swears.txt"
      short-code-and-words-list (get-server-short-code-and-words-list filename)
      all-routes (routes
        (GET "/v1/generate-level" [] (fn [req] (generate-level req short-code-and-words-list)))
        (route/resources "/") ;; static file url prefix /static, in `public` folder
        (route/not-found "<p>Page not found.</p>")) ;; all other, return 404
    ]
    (println "Server running")
    (run-server all-routes {:port 8080})
  )
)