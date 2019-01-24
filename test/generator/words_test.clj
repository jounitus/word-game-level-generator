(ns generator.words-test
  (:require [clojure.test :refer :all] :require [generator.words :refer :all])
  )

(deftest tuple-vector-to-map-test
  (let
    [
      l [
          ["a", 1]
          ["b", 2]
          ["c", 3]
          ["b", 4]
          ["d", 5]
          ["a", 6]
      ]
      result {"a" [6 1], "b" [4 2], "c" [3], "d" [5]}
    ]
    (is (= (tuple-vector-to-map l) result))
  )
)

(deftest get-short-code-and-words-list-test

  (let
    [
      l [
          "abba"
          "abbath"
          "baba"
          "moot"
          "tomb"
          "bottom"
      ]
      result [
        [#{\a \b} ["baba" "abba"]]
        [#{\a \b \h \t} ["abbath"]]
        [#{\m \o \t} ["moot"]]
        [#{\b \m \o \t} ["bottom" "tomb"]]
        ]
    ]
    (is (= (get-short-code-and-words-list l) result))
  )
)

(deftest get-short-code-to-short-codes-list-test
  (let
    [
      l [
          #{\a \b}
          #{\a \b \h \t}
          #{\m \o \t}
          #{\b \m \o \t}
      ]
      result [
        [#{\a \b} [#{\a \b}]]
        [#{\a \b \h \t} [#{\a \b \h \t} #{\a \b}]]
        [#{\m \o \t} [#{\m \o \t}]]
        [#{\b \m \o \t} [#{\b \m \o \t} #{\m \o \t}]]
        ]
    ]
    (is (= (get-short-code-to-short-codes-list l) result))
  )
)

(deftest get-combined-short-codes-to-words-list-test

  (let
    [

      l1 [
        [#{\a \b} [#{\a \b}]]
        [#{\a \b \h \t} [#{\a \b \h \t} #{\a \b}]]
        [#{\m \o \t} [#{\m \o \t}]]
        [#{\b \m \o \t} [#{\b \m \o \t} #{\m \o \t}]]
        ]

      l2 [
        [#{\a \b} ["baba" "abba"]]
        [#{\a \b \h \t} ["abbath"]]
        [#{\m \o \t} ["moot"]]
        [#{\b \m \o \t} ["bottom" "tomb"]]
      ]
      result [
        [#{\a \b \h \t} #{"baba" "abbath" "abba"}]
        [#{\b \m \o \t} #{"tomb" "moot" "bottom"}]
        [#{\a \b} #{"baba" "abba"}]
        [#{\m \o \t} #{"moot"}]
        ]
    ]
    (is (= (get-combined-short-codes-to-words-list l1 l2) result))

  )
)

(deftest get-longer-short-code-and-words-list-test

  (let
    [
      l [
          "abba"
          "abbath"
          "baba"
          "moot"
          "tomb"
          "bottom"
          "abcdefghijklmnopqrstuvwxyz"
      ]

      result [
        [#{\a \b \h \t} #{"baba" "abbath" "abba"}]
        [#{\b \m \o \t} #{"tomb" "moot" "bottom"}]
        [#{\a \b} #{"baba" "abba"}]
        [#{\m \o \t} #{"moot"}]
        ]
    ]
    (is (= (get-longer-short-code-and-words-list 7 l) result))
  )
)

(run-tests)