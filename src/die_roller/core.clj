(ns die-roller.core
  (:require [clojure.spec.alpha :as s]))

(s/def ::count pos-int?)
(s/def ::faces pos-int?)
(s/def ::roll int?)
(s/def ::rolls (s/coll-of ::roll))
(s/def ::best-of (s/nilable nat-int?))
(s/def ::worst-of (s/nilable nat-int?))
(s/def ::modifier (s/nilable int?))

(s/def ::die-expr string?)
(s/def ::parsed-expr (s/nilable (s/tuple ::count ::faces ::best-of ::worst-of ::modifier)))

(def expr-re #"^(\d+?)d(\d+)(b(\d+?))?(w(\d+?))?([-+]\d+)?$")

(defn parse-expr [expr]
  (when-let [[_ count faces _ best-of _ worst-of modifier]
             (re-matches expr-re expr)]
    (vec
     (map
      #(when % (Integer/parseInt %))
      [count faces best-of worst-of modifier]))))

(s/fdef parse-expr
  :args (s/cat :expr ::die-expr)
  :ret ::parsed-expr)

(defn roll-die [n]
  (+ 1 (rand-int n)))

(s/fdef roll-die
  :args (s/cat :n ::faces)
  :ret ::roll)

(defn do-rolls [count faces best-of worst-of modifier]
  (let [rolls
        (sort
         (for [_ (range count)]
           (+ (or modifier 0)
              (roll-die faces))))]
    (->> rolls
         (take-last (or best-of count))
         (take (or worst-of count)))))

(s/fdef do-rolls
  :args (s/cat :count ::count
               :faces ::faces
               :best-of ::best-of
               :worst-of ::worst-of
               :modifier ::modifier)
  :ret ::rolls)

(defn do-expr [expr]
  (when-let [result (parse-expr expr)]
    (apply do-rolls result)))

(s/fdef do-expr
  :args (s/cat :expr ::die-expr)
  :ret (s/nilable ::rolls))
