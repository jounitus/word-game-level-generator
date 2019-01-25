(ns generator.level
  (:require [generator.core :refer :all]))

(def NO_GO "#")

(defn get-bounds
  "returns :min :max (and :size) bounds for vector of 2d tuple coordinates [[0, 3] [-5, 1]]"
  [coll]
  (let
    [
      min-x (reduce min (map first coll))
      min-y (reduce min (map second coll))
      max-x (reduce max (map first coll))
      max-y (reduce max (map second coll))
    ]
    {:min [min-x min-y] :max [max-x max-y] :size [(- max-x min-x) (- max-y min-y)]}
  )
)

(defn render-level
  "renders simple ascii version of the level, might be useful for debugging"
  [l]
  (let
    [
      bounds (get-bounds (map first l))
      [min-x min-y] (:min bounds)
      [max-x max-y] (:max bounds)
    ]
    (for [y (range min-y (inc max-y))]
      (clojure.string/join
        (for [x (range min-x (inc max-x))]
          (get l [x, y] " ")
        )
      )
    )
  )
)

(defn get-multipliers-for-direction
  "returns 2d vector coordinates based on direction [1 0] for horizontal [0 1] for vertical"
  [horizontal?]
  (if horizontal? [1, 0] [0, 1])
)

(defn normalize-coordinate
  "used to normalize level coordinates, so that every level have min x and min y coordinates of 0"
  [[x, y] horizontal? offset]

  (let [[x-mul y-mul] (get-multipliers-for-direction horizontal?)]
    [
      (+ x (- (* offset x-mul))) ; (- xxx) is to negate the offset
      (+ y (- (* offset y-mul)))
    ]
  )

)

(defn grid-get-coordinates
  "returns coordinates where the letters can be plotted on the map"
  (
    [[x y] [x-mul y-mul] word]

    (into {}

      (for [n (range(count word)) :let [

        cur-x (+ x (* n x-mul))
        cur-y (+ y (* n y-mul))

        letter (get word n)]]

        [[cur-x cur-y] letter]

      )
    )
  )
  (
    [position horizontal? offset word]

    (grid-get-coordinates
      (normalize-coordinate position horizontal? (+ offset 1)) ; compensate offset because of the NO_GO
      (get-multipliers-for-direction horizontal?)
      (vec (concat [NO_GO] word [NO_GO]))
    )

  )
)


(defn get-letter-match-score
  "used to calculate if we can plot a letter to a specific place on a map"
  [map-letter letter]
  (cond
    ; two NO_GO letters match, it's ok
    (and (= map-letter letter) (= map-letter NO_GO)) 0
    ; real letters match
    (= map-letter letter) 1
    ; we can fit our letter here because map letter is nil
    (and (not (= map-letter letter)) (= map-letter nil)) 0
    ; this means our letter differs with the one on the map, it's bad
    :else nil
  )
)


(defn get-coordinates-with-match-score
  "match two vectors of coordinates and letters and return 'match score' for every coordinates that match"
  [m letter-coordinates]
  (map
    (fn [[coordinate letter]]
      (let [map-letter (get m coordinate)]
        [coordinate (get-letter-match-score map-letter letter)]
      )
    ) letter-coordinates
  )
)


(defn get-matching-coordinates
  "returns all the coordinates where match score is 1 or returns simply nil if any of the match scores is nil"
  [letter-coordinates-with-match]
  (let
    [
      scores (map second letter-coordinates-with-match)
    ]
    (cond
      ; one nil or more means we can't plot the word
      (> (count (filter nil? scores)) 0) nil
      ; else sum all the values to get the score
      :else (set (map first (filter (fn [[coordinate score]] (> score 0)) letter-coordinates-with-match)))
    )
  )
)


(defn test-valid-coordinate
  "in some cases the x or y position must be even,
  we do this just so that algorithm for placing the possible words
  to the grid is a bit simpler

  this way we don't have to check neighbours for every letter
  we plot to the grid"

  [coordinate horizontal?]

  (if horizontal?
    ; if placing words horizontally, the y-position must be even
    (even? (get coordinate 1))
    ; if placing words vertically, the x-position must be even
    (even? (get coordinate 0))
  )

)

(defn get-valid-coordinates-for-letter
  "based on are plotting a word horizontally or vertically,
  returns all the coordinates from the map, where there is a matching letter"
  [m letter horizontal?]

  ; return only the first element (the coordinates), ignore the letter
  (map #(first %)

    (filter
      #(and
        ; test matching letter
        (= letter (last %))
        ; and test that the coordinate is valid
        (test-valid-coordinate (first %) horizontal?)
      )

      (vec m) ; pass the map as a vector
    )
  )

)

(defn by-score
  "sort first by number of matches (descending, so the words with most matches are on top),
  then by other fields ascending"
  [x y]

  ; https://clojure.org/guides/comparators

  (compare [(count (:matching-coordinates y)) (:coordinate x) (:offset x)]
           [(count (:matching-coordinates x)) (:coordinate y) (:offset y)]))

(defn try-fit-single-word
  "Tries to insert a word to the map every possible way.
  Returns the best match according to amount of letters that this word matches on the map"
  (
  [m horizontal? offset coordinate word]
    (let [
      coordinates (grid-get-coordinates coordinate horizontal? offset word)
      coordinates-with-match-score (get-coordinates-with-match-score m coordinates)
      matching-coordinates (get-matching-coordinates coordinates-with-match-score)
      ]
      ;(prn "try-fit-single-word 3" score coordinate horizontal? offset word)
      {
        :matching-coordinates matching-coordinates
        :coordinate (normalize-coordinate coordinate horizontal? offset)
        :coordinates coordinates
        ;:horizontal? horizontal?
        ;:offset offset
        ;:word word
      }
    )
  )
  (
  [m horizontal? offset word]

    (doall
      (map
        (fn [coordinate]
          (try-fit-single-word m horizontal? offset coordinate word)
        )
        (get-valid-coordinates-for-letter m (get word offset) horizontal?)
      )
    )

  )
  (
  [m horizontal? word]
    (doall
      (->> (range (count word))
           (map #(try-fit-single-word m horizontal? % word))
           (apply concat) ; flatten results one level
           (sort by-score)
      )
    )
  )
)



(defn try-build-level
  "try building a level based on all the words provided"
  (
    [n words result]
    (let [
          word (first words)
          horizontal? (even? n)
          try-fit-result (first (try-fit-single-word (:map result) horizontal? word))
          ]
      (cond
        (= 0 (count words))
          (do (prn "finished!") result)
        (nil? (:matching-coordinates try-fit-result))
          (do (prn "no matching coordinates!") result)
        :else
          (let
            [
              result (update-in result [:map] merge (:coordinates try-fit-result))
              result (update-in result [:words] conj {
                :word word
                :coordinate (:coordinate try-fit-result)
                :horizontal? horizontal?
              })
              result (update-in result [:matching-coordinates] into (:matching-coordinates try-fit-result))
            ]

            (do
              (prn "try-build-level" word n horizontal? try-fit-result result)

              (recur (inc n) (rest words) result)
            )
          )
      )
    )
  )

  (
    [words]
    (let [result {
      :map (grid-get-coordinates [0, 0] true 0 (first words))
      :matching-coordinates #{}
      :words [{
                :word (first words)
                :coordinate [0, 0]
                :horizontal? true
              }]
      }]
      (try-build-level 1 (rest words) result)
    )
  )
)

(defn op2d
  "do 2d math operations like + - between two 2d tuples"
  [f [x1, y1] [x2, y2]]
  [(f x1 x2) (f y1 y2)]
)

(defn fix-and-clean-word
  "'normalize' the coordinates based on the offset parameter"
  [word, offset]
  (assoc word
    :coordinate (op2d - (:coordinate word) offset)
  )
)

(defn clean-level
  "Normalize all coordinates, so that min x and min y coordinates are always 0.
  Also change map coordinates, so that it converts better into a json array."
  [level]
  (let
    [
      l (vec (:map level)) ; convert the map to list
      l (filter #(not(= (second %) NO_GO)) l) ; remove all the NO_GOs
      {min-bounds :min} (get-bounds (map first l))

      ; normalize map coordinates to min being [0 0]

      l (map (fn [[coord letter]] [(op2d - coord min-bounds) letter]) l)
      level (assoc level :map l)

      ; normalize matching coordinates

      matching-coordinates (map #(op2d - % min-bounds) (:matching-coordinates level))
      level (assoc level :matching-coordinates matching-coordinates)

      ; fix and normalize words

      level (assoc level :words (map #(fix-and-clean-word % min-bounds) (:words level)))

    ]
    level
  )
)
