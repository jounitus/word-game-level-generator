(ns generator.core)


(defn test-great-word
  "tests if a word is something we could use"
  [word]
  (>= (count word) 4)
  )

(defn read-lines-from-file
  "reads lines lazily from a file"
  [filename]
  (with-open [rdr (clojure.java.io/reader filename)]
     (doall (line-seq rdr))))

(defn find-matching-short-codes
  "find which 'short-codes' partially match 'short-code'
    'short-codes' must be shorter than the 'short-code' to match"

  [short-code short-codes]

  (conj
    (filter
      (fn [val]
        (and (< (count val) (count short-code)) (every? short-code val))
      )
      short-codes
    )
    short-code
  )

)

(defn find-and-merge-words
  "find all the words that match the short-codes and builds combined short-code from those words"
  [short-codes l]

  (->> l
       (filter (fn [[short-code words]] (contains? short-codes short-code)))
       (map second)
       (flatten)
       (set)
       )

)

(defn subtract-collection
  [coll1 coll2]
  "removes items in coll2 from coll1"
  (filter #(not (.contains coll2 %)) coll1)
)

(defn generate-combinations
  (
    [previous coll]
    "
    simplified version of this method:

      (for [a [0 1 2]]
        (for [b [0 1 2]]
          (for [c [0 1 2]]
            [a, b, c]
          )
        )
      )

    "
    ;(prn "gen" previous how-many counter)
    (let [counter (- (count coll) (count previous))]
      (if (> counter 0)
        (let
          [
            result
              (for [x (subtract-collection coll previous)]
                (generate-combinations (conj previous x) coll)
              )
          ]
          (if (> counter 1) (mapcat identity result) result)
          ;result
        )
        (do
          ;(prn "returning previous" previous)
          previous
        )
      )
    )
  )
  (
    [coll]
    "
    Generate different combinations of collections

    For example
      (generate-combinations (range 3))

    generates:
      [0 1 2]
      [0 2 1]
      [1 0 2]
      [1 2 0]
      [2 0 1]
      [2 1 0]

    "
    ;(filter (fn [coll] (valid-set? coll how-many)) (gen [] how-many how-many (range how-many)))
    (generate-combinations [] coll)
  )
)
