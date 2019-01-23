(ns generator.level-test
  (:require [clojure.test :refer :all]
            [generator.core :refer :all]
            [generator.level :refer :all]))

(deftest get-bounds-test
  (let
    [
      l [
          [3, 5]
          [-3, 4]
          [7, 11]
          [3, -9]
          [4, 5]
          [-2, 7]
      ]
      result {:min [-3 -9], :max [7 11], :size [10 20]}
    ]
    (is (= (get-bounds l) result))
  )
)

(deftest render-level-test
  (let
    [
      l {
          [2 -2] "e", [2 2] "l", [0 0] "e", [2 -1] "#", [1 0] "l", [2 3] "l", [7 2] "#",
          [0 -4] "#", [0 -2] "a", [-1 0] "#", [6 3] "s", [3 -2] "#", [4 2] "a", [3 0] "e",
          [0 -3] "s", [6 -1] "#", [5 2] "s", [-1 -2] "e", [6 4] "#", [6 1] "e", [2 4] "#",
          [2 0] "s", [2 1] "e", [0 -1] "l", [6 2] "e", [6 0] "s", [1 2] "#", [1 -2] "s",
          [3 2] "e", [0 1] "#", [-2 -2] "#", [4 0] "#"
      }
      result (render-level l)
      expected [
        "  #       "
        "  s       "
        "#ease#    "
        "  l #   # "
        " #else# s "
        "  # e   e "
        "   #lease#"
        "    l   s "
        "    #   # "
      ]
    ]
    (do
      (println (clojure.string/join "\n" result))
      (is (= result expected))
    )
  )
)

(deftest get-multipliers-for-direction-test

  (is (= (get-multipliers-for-direction true)
         [1, 0]
         ))

  (is (= (get-multipliers-for-direction false)
         [0, 1]
         ))

)

(deftest normalize-coordinate-test

  (is (= (normalize-coordinate [3, 4] true 2)
         [1, 4]
         ))

  (is (= (normalize-coordinate [5, 6] false 3)
         [5, 3]
         ))

)

(deftest grid-get-coordinates-test

  (is (= (grid-get-coordinates [-1 5] [1 0] ["l" "o" "l"])
         {[-1 5] "l" [0 5] "o" [1 5] "l"}
      ))

  (is (= (grid-get-coordinates [2 5] true 3 ["l" "o" "l"])
         {[-2 5] "#" [-1 5] "l" [0 5] "o" [1 5] "l" [2 5] "#"}
      ))

  (is (= (grid-get-coordinates [2 2] [0 1] ["l" "o" "l"])
         {[2 2] "l" [2 3] "o" [2 4] "l"}
      ))

  (is (= (grid-get-coordinates [2 5] false 3 ["l" "o" "l"])
         {[2 1] "#" [2 2] "l" [2 3] "o" [2 4] "l" [2 5] "#"}
      ))

)

(deftest get-letter-match-score-test

  (is (= (get-letter-match-score "a" "a") 1))
  (is (= (get-letter-match-score "b" "b") 1))
  (is (= (get-letter-match-score NO_GO NO_GO) 0))
  (is (= (get-letter-match-score "#" "#") 0))
  (is (= (get-letter-match-score "b" "a") nil))
  (is (= (get-letter-match-score "b" NO_GO) nil))
  (is (= (get-letter-match-score NO_GO "a") nil))
  (is (= (get-letter-match-score nil "a") 0))

)

(deftest get-coordinates-with-match-score-test

  (let [m {[-1 0] "#", [0 0] "d", [1 0] "o", [2 0] "l", [3 0] "l", [4 0] "s", [5 0] "#"}]

    (is (= (get-coordinates-with-match-score m [[[2 0] "l"] [[3 0] "l"]])
           [[[2 0] 1] [[3 0] 1]]))

    (is (= (get-coordinates-with-match-score m [[[-1 0] "l"] [[0 0] "d"]])
           [[[-1 0] nil] [[0 0] 1]]))

    (is (= (get-coordinates-with-match-score m [[[3 -1] "l"] [[3 0] "l"]])
           [[[3 -1] 0] [[3 0] 1]]))

  )
)

(deftest get-matching-coordinates-test

  (is (= (get-matching-coordinates [[[2 0] 1] [[3 0] 1]]) #{[2 0] [3 0]}))

  (is (= (get-matching-coordinates [[[-1 0] nil] [[0 0] 1]]) nil))

  (is (= (get-matching-coordinates [[[3 -1] 0] [[3 0] 1]]) #{[3 0]}))

)

(deftest test-valid-coordinate-test

  (is (= (test-valid-coordinate [-1, 0] true) true))
  (is (= (test-valid-coordinate [0, 0] true) true))
  (is (= (test-valid-coordinate [1, 0] true) true))

  (is (= (test-valid-coordinate [-1, 1] true) false))
  (is (= (test-valid-coordinate [0, 1] true) false))
  (is (= (test-valid-coordinate [1, 1] true) false))


  (is (= (test-valid-coordinate [0, -1] false) true))
  (is (= (test-valid-coordinate [0, 0] false) true))
  (is (= (test-valid-coordinate [0, 1] false) true))

  (is (= (test-valid-coordinate [1, -1] false) false))
  (is (= (test-valid-coordinate [1, 0] false) false))
  (is (= (test-valid-coordinate [1, 1] false) false))

)

(deftest get-valid-coordinates-for-letter-test

  (let [m {[-1 0] "#", [0 0] "d", [1 0] "o", [2 0] "l", [3 0] "l", [4 0] "s", [5 0] "#"}]

    (is (= (get-valid-coordinates-for-letter m "l" true) [[2 0] [3 0]]))
    (is (= (get-valid-coordinates-for-letter m "l" false) [[2 0]]))

  )
)

(deftest try-fit-single-word-test

  (let
    [
      m { [-1 0] "#", [0 0] "l", [1 0] "x", [2 0] "o", [3 0] "#" }

      result [
        {:matching-coordinates #{[0 0]},
          :coordinate [0 -2],
          :coordinates {[0 -3] "#", [0 -2] "l", [0 -1] "o", [0 0] "l", [0 1] "#"}}
        {:matching-coordinates #{[0 0]},
          :coordinate [0 0],
          :coordinates {[0 -1] "#", [0 0] "l", [0 1] "o", [0 2] "l", [0 3] "#"}}
        {:matching-coordinates #{[2 0]},
          :coordinate [2 -1],
          :coordinates {[2 -2] "#", [2 -1] "l", [2 0] "o", [2 1] "l", [2 2] "#"}}
      ]
    ]

    (is (= (try-fit-single-word m false ["l" "o" "l"]) result))

  )

  (let
    [
      m { [-1 0] "#", [0 0] "l", [1 0] "o", [2 0] "b", [3 0] "#",
          [-1 2] "#", [0 2] "l", [1 2] "o", [2 2] "b", [3 2] "#"},

      result [
        {:matching-coordinates #{[0 0] [0 2]},
          :coordinate [0 0],
          :coordinates {[0 -1] "#", [0 0] "l", [0 1] "o", [0 2] "l", [0 3] "#"}}
        {:matching-coordinates #{[0 0] [0 2]},
          :coordinate [0 0],
          :coordinates {[0 -1] "#", [0 0] "l", [0 1] "o", [0 2] "l", [0 3] "#"}} ; TODO remove duplicates
        {:matching-coordinates #{[0 0]},
          :coordinate [0 -2],
          :coordinates {[0 -3] "#", [0 -2] "l", [0 -1] "o", [0 0] "l", [0 1] "#"}}
        {:matching-coordinates #{[0 2]},
          :coordinate [0 2],
          :coordinates {[0 1] "#", [0 2] "l", [0 3] "o", [0 4] "l", [0 5] "#"}}
      ]
    ]

    (is (= (try-fit-single-word m false ["l" "o" "l"]) result))

  )
)

(deftest try-build-level-test

  (let [split-words (vec (map split-word
    ["else" "sale" "ease" "sell" "lease" "sees" "less" "seal" "sales" "assess" "sells" "seas"]
    ))]

    (prn split-words)
    (let [
      result (try-build-level split-words)
      expected-map {
        [2 -2] "e", [2 2] "l", [0 0] "e", [2 -1] "#", [1 0] "l", [2 3] "l", [7 2] "#",
        [0 -4] "#", [0 -2] "a", [-1 0] "#", [6 3] "s", [3 -2] "#", [4 2] "a",
        [3 0] "e", [0 -3] "s", [6 -1] "#", [5 2] "s", [-1 -2] "e", [6 4] "#",
        [6 1] "e", [2 4] "#", [2 0] "s", [2 1] "e", [0 -1] "l", [6 2] "e", [6 0] "s",
         [1 2] "#", [1 -2] "s", [3 2] "e", [0 1] "#", [-2 -2] "#", [4 0] "#"
      }
      expected-matching-coordinates #{[0 0] [0 -2] [2 0] [2 2] [6 2]}
      expected-words [
        {:word ["e" "l" "s" "e"], :coordinate [0 0], :horizontal? true}
        {:word ["s" "a" "l" "e"], :coordinate [0 -3], :horizontal? false}
        {:word ["e" "a" "s" "e"], :coordinate [-1 -2], :horizontal? true}
        {:word ["s" "e" "l" "l"], :coordinate [2 0], :horizontal? false}
        {:word ["l" "e" "a" "s" "e"], :coordinate [2 2], :horizontal? true}
        {:word ["s" "e" "e" "s"], :coordinate [6 0], :horizontal? false}
        ]
      ]
      (is (= (get result :map) expected-map))
      (is (= (get result :matching-coordinates) expected-matching-coordinates))
      (is (= (get result :words) expected-words))
    )

  )
)

(deftest op2d-test

  (is (= (op2d + [2 3] [1 2]) [3 5]))
  (is (= (op2d - [2 3] [1 2]) [1 1]))
  (is (= (op2d * [2 3] [1 2]) [2 6]))

)

(deftest clean-level-test

  (let [split-words (vec (map split-word
    ["else" "sale" "ease" "sell" "lease" "sees" "less" "seal" "sales" "assess" "sells" "seas"]
    ))]

    (prn split-words)
    (let [
      result (clean-level (try-build-level split-words))
      result-coordinates (map first (:map result))
      expected-map [
        [[3 1] "e"] [[3 5] "l"] [[1 3] "e"] [[2 3] "l"] [[3 6] "l"] [[1 1] "a"]
        [[7 6] "s"] [[5 5] "a"] [[4 3] "e"] [[1 0] "s"] [[6 5] "s"] [[0 1] "e"]
        [[7 4] "e"] [[3 3] "s"] [[3 4] "e"] [[1 2] "l"] [[7 5] "e"] [[7 3] "s"]
        [[2 1] "s"] [[4 5] "e"]
      ]
      expected-matching-coordinates [[3 5] [1 3] [1 1] [3 3] [7 5]]
      expected-words [
        {:word "else", :coordinate [1 3], :horizontal? true}
        {:word "sale", :coordinate [1 0], :horizontal? false}
        {:word "ease", :coordinate [0 1], :horizontal? true}
        {:word "sell", :coordinate [3 3], :horizontal? false}
        {:word "lease", :coordinate [3 5], :horizontal? true}
        {:word "sees", :coordinate [7 3], :horizontal? false}
        ]
      ]
      (is (= (get result :map) expected-map))
      (is (= (get result :matching-coordinates) expected-matching-coordinates))
      (is (= (get result :words) expected-words))
      (is (= (apply min (map first result-coordinates)) 0)) ; smallest x coordinate must be 0
      (is (= (apply min (map second result-coordinates)) 0)) ; smallest y coordinate must be 0
    )
  )
)

(run-tests)