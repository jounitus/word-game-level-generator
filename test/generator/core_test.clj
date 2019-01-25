(ns generator.core-test
  (:require [clojure.test :refer :all]
            [generator.core :refer :all]))


(deftest test-great-word-test
  (is (= (test-great-word "astronomical") true))
  (is (= (test-great-word "boo") false)) ; too short
)


(deftest read-lines-from-file-test
  (let [filename "../../resources/google-10000-english-no-swears.txt"]
     (prn (read-lines-from-file filename))
     (is (= (count (read-lines-from-file filename)) 9897))
     )
)

(deftest find-matching-short-codes-test
  (let [short-code #{\a \b \c}
        short-codes [#{\a \b} #{\b \c} #{\b \c} #{\a \b \c} #{\a \b \c \d}]
        result [#{\a \b \c} #{\a \b} #{\b \c} #{\b \c}]
        ]
    (is (= (find-matching-short-codes short-code short-codes) result)))
)

(deftest find-and-merge-words-test
  (let [short-codes #{"a" "b" "c"}
        l [
          ["a" ["a"]]
          ["b" ["a" "b"]]
          ["c" ["b" "c"]]
          ["d" ["x" "y"]]
        ]
        result #{"a" "b" "c"}
       ]
    (is (= (find-and-merge-words short-codes l) result)))

  (let [short-codes #{"a" "b" "c"}
        l [
          ["a" ["a"]]
          ["b" ["a" "b"]]
          ["d" ["x" "y"]]
        ]
        result #{"a" "b"}
       ]
    (is (= (find-and-merge-words short-codes l) result)))

)

(deftest subtract-collection-test
  (let [coll1 ["a", "b", "b", "c", "d"]
        coll2 ["b", "d"]
        expected ["a", "c"]
       ]
    (is (= (subtract-collection coll1 coll2) expected))
  )
)

(deftest generate-combinations-test
  (let [
        expected [[0 1 2] [0 2 1] [1 0 2] [1 2 0] [2 0 1] [2 1 0]]
        ; this is the more "dynamic" expected result, use this if you are paranoid
        ; about what the results should be
        expected2
          (sort
            (set
              ; assume that after 10000 repeats, you have a high chance of getting all possible combinations
              (repeatedly 10000
                #(shuffle [0,1,2])
              )
            )
          )
       ]
    (is (= (generate-combinations (range 3)) expected))
    (is (= (generate-combinations (range 3)) expected2))
  )
  (let [
        expected [
          [0 1 2 3] [0 1 3 2] [0 2 1 3] [0 2 3 1] [0 3 1 2] [0 3 2 1]
          [1 0 2 3] [1 0 3 2] [1 2 0 3] [1 2 3 0] [1 3 0 2] [1 3 2 0]
          [2 0 1 3] [2 0 3 1] [2 1 0 3] [2 1 3 0] [2 3 0 1] [2 3 1 0]
          [3 0 1 2] [3 0 2 1] [3 1 0 2] [3 1 2 0] [3 2 0 1] [3 2 1 0]]
       ]
    (is (= (generate-combinations (range 4)) expected))
  )
)

(run-tests)

