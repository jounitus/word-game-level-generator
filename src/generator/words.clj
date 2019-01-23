(ns generator.words
  (:require [generator.core :refer :all]))


(defn tuple-vector-to-map
  "converts vector of tuples into a map with map key being the first value of the tuple
  and the value being all the second values in tuple containing the same first value"
  [tuple-vector]
  (reduce (fn [m [first-val second-val]] (update-in m [first-val] #(conj % second-val))) {} tuple-vector)
)


(defn get-short-code-and-words-list
  "converts list of words into a list of tuples, where the first tuple value is
  all the unique letters in the words that are in the second tuple value.
  The second tuple value can contain multiple wods"
  [words]
  (let
    [
      short-code-and-word (map #(vec [(set %) %]) words) ; ([#{\a \b} "abba"] [#{\a \b \h \t} "abbath"])
      short-code-map (tuple-vector-to-map short-code-and-word)
      short-code-list (map identity short-code-map) ; convert map to list of (key value) tuples
    ]

    short-code-list

  )
)

(defn get-short-code-to-short-codes-list
  "returns tuple list, where first tuple value is a set of unique letters and
  the second tuple value is set of unique letters that can be build
  with the first tuple value"
  [short-codes]

  (for [short-code short-codes
          :let [result (find-matching-short-codes short-code short-codes)]
       ]
    [(first result) result]
  )

)

(defn get-combined-short-codes-to-words-list
  "returns tuple list, where first tuple value is a set of unique letters
  and the second tuple value is list of all the words that can be built
  with those set of unique letter

  The first argument should be the output of 'get-short-code-to-short-codes-list' function
  The second argument should be the output of 'get-short-code-and-words-list' function
  "

  [short-code-to-short-codes-list short-code-and-words-list]
  (let
    [
      longer-short-code-and-words-list
        (for [[short-code short-codes] short-code-to-short-codes-list]
          [short-code (find-and-merge-words (set short-codes) short-code-and-words-list)]
        )
    ]
    (sort (fn [[akey aval], [bkey bval]] (compare (count bval) (count aval))) longer-short-code-and-words-list)
  )

)

(defn get-longer-short-code-and-words-list
  "from a list of words, return a tuple list where the first tuple value is a short code
  and the second tuple value is a list of all the words that can be made with the short code.

  NOTE: the second argument max-short-code-length is to prevent list, where the short code is long
  and the word list is huge and contain seemingly random words
  "

  [words max-short-code-length]
  (let
    [

      ;
      ; build a list with short-code and words
      ;

      short-code-and-words-list (get-short-code-and-words-list words)

      ;
      ; filter the short-codes by length (so that it doesn't take that long to build the final list)
      ;

      short-codes (set (map first short-code-and-words-list))

      filtered-short-codes (filter #(<= (count %) max-short-code-length) short-codes)

      ;
      ; now see what short code match with a longer short codes
      ; (for example [a b c d] matches with [a b d] and [a c d]
      ;

      short-code-to-short-codes-list (get-short-code-to-short-codes-list filtered-short-codes)

      ;(doall (map prn short-code-to-short-codes-list))

      ;
      ; build the final list that has the short codes and the words from shorter short codes in it
      ;

      longer-short-code-and-words-list
        (get-combined-short-codes-to-words-list short-code-to-short-codes-list short-code-and-words-list)

    ]

    longer-short-code-and-words-list

  )
)
