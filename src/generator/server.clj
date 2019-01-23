(ns generator.server
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

(println "Loading words...")

(defn split-all-words
  [words]
  (map split-word words)
)

(def filename "../../resources/google-10000-english-no-swears.txt")
(def words (filter test-great-word (read-lines-from-file filename)))
(def short-code-and-words-list
  (map (fn [[short-code words]] [short-code (split-all-words words)])
    (filter (fn [[short-code words]] (>= (count words) 5))
      (get-longer-short-code-and-words-list words 5)
    )
  )
)

(println "Done loading words.")

(defn app [req]
  (let
    [
      [short-code split-words] (rand-nth short-code-and-words-list)
      split-words (shuffle split-words)
      level (try-build-level split-words)
      level (clean-level level)
    ]
    {:status  200
     :headers {"Content-Type" "application/json"}
     :body    (generate-string level)
    }
  )
)

(println "Server now running")

(defroutes all-routes
  (GET "/v1/generate-level" [] app) ;; asynchronous(long polling)
  (route/resources "/") ;; static file url prefix /static, in `public` folder
  (route/not-found "<p>Page not found.</p>")) ;; all other, return 404

(run-server all-routes {:port 8080})