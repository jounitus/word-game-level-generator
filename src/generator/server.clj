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
  []
  (let
    [
      filename "./resources/google-10000-english-no-swears.txt"
      words (filter test-great-word (read-lines-from-file filename))
      short-code-and-words-list
        (map (fn [[short-code words]] [short-code (split-all-words words)])
          (filter (fn [[short-code words]] (>= (count words) 5))
            (get-longer-short-code-and-words-list words 5)
          )
        )
    ]
    short-code-and-words-list
  )

)

(defn generate-level
  [req short-code-and-words-list]
  (let
    [
      [short-code split-words] (rand-nth short-code-and-words-list)
      split-words (shuffle split-words)
      level (try-build-level split-words)
      level (clean-level level)
    ]
    {
      :status  200
      :headers {"Content-Type" "application/json"}
      :body    (generate-string level)
    }
  )
)

(defn -main [& args]
  (println "Reading words...")
  (let
    [
      short-code-and-words-list (get-server-short-code-and-words-list)
      all-routes (routes
        (GET "/v1/generate-level" [] (fn [req] (generate-level req short-code-and-words-list)))
        (route/resources "/") ;; static file url prefix /static, in `public` folder
        (route/not-found "<p>Page not found.</p>")) ;; all other, return 404
    ]
    (println "Server running")
    (run-server all-routes {:port 8080})
  )
)