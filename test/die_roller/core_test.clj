(ns die-roller.core-test
  (:require [clojure.spec.alpha :as s]
            [orchestra.spec.test :refer [instrument]]
            [clojure.test :refer [deftest is testing]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as props]
            [die-roller.core :as core]))

(instrument)

;; let's start with some example tests
;; which test explicit presumptions.

(deftest example-example-test
  (testing "2d1 > 1d1"
    (is (> (reduce + (core/eval-die-expr "2d1"))
           (reduce + (core/eval-die-expr "1d1")))))
  (testing "Doesn't blow up on complex input"
    (let [result (core/eval-die-expr "3d20b2w1-1")]
      (and (is (= 1 (count result)))
           (is (>= 19 (first result) 0))))))

;; but this only tests the cases we bothered to implement!
;; what about when inputs are larger, more... random?

(defspec examples-but-specd 20
  (props/for-all
   [count* (s/gen ::core/count)
    faces (s/gen ::core/faces)
    modifier (s/gen pos-int?)]
   (let [best-of (max 1 (rand-int count*))
         worst-of 1
         expr (format "%sd%sb%sw%s-%s"
                      count* faces best-of worst-of modifier)
         result (core/eval-die-expr expr)]
     (and (is (= 1 (count result)))
          (is (>= (- faces modifier -1) (first result) (- 1 modifier)))))))

;; the generator above only uses specs sometimes
;; because the test doesn't expect potentially-nil values
;; (which our specs do)

;; the following tests use specs both to generate input
;; and validate results.
;; this has the drawback of obscuring whether the result is "correct"
;; because the result might be so many things!
;; but we can comfortably assert that the result fits data expectations.

(defspec make-expr-test 500
  (props/for-all
   [args (s/gen ::core/expr-parts)]
   (let [result (apply core/make-expr args)]
     (s/valid? ::core/die-expr result))))

(defspec parse-expr-test 500
  (props/for-all
   [expr (s/gen ::core/die-expr)]
   (let [result (core/parse-expr expr)]
     (s/valid? ::core/expr-parts result))))

(defspec roll-die-test 500
  (props/for-all
   [n (s/gen ::core/faces)]
   (let [result (core/roll-die n)]
     (s/valid? ::core/roll result))))

;; the following test only uses 20 cases
;; because as cases increase, inputs get more complicated
;; and large die rolls are computationally expensive.
;; (large meaning more than a million dice with more than a million faces.)
;; still, it will drill down on test failures to find edge cases

(defspec eval-rolls-test 20
  (props/for-all
   [[count* faces best-of worst-of modifier]
    (s/gen ::core/expr-parts)]
   (and (let [result (core/eval-rolls count* faces best-of worst-of modifier)]
          (s/valid? ::core/rolls result))
        (let [result (core/eval-rolls count* 1 nil nil nil)]
          (= count* (reduce + result)))
        (let [result (core/eval-rolls 1 faces nil nil nil)]
          (<= 1 (first result) faces))
        (if modifier
          (let [result (core/eval-rolls 1 1 nil nil modifier)]
            (= (first result) (+ 1 modifier)))
          true)
        (if best-of
          (let [result (core/eval-rolls count* 1 best-of nil nil)]
            (= (count result) (min count* best-of)))
          true)
        (if worst-of
          (let [result (core/eval-rolls count* 1 nil worst-of nil)]
            (= (count result) (min count* worst-of)))
          true))))

;; this test generates strings and evaluates them as expressions,
;; but it's rather unlikely that generated strings are valid expressions.
;; that's fine! we've already tested everything else.
;; this test validates only that unruly user input won't break the system.

(defspec eval-expr-test 500
  (props/for-all
   [expr (s/gen string?)]
   (s/valid? (s/nilable ::core/rolls) (core/eval-die-expr expr))))
