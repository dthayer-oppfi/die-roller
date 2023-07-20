(ns die-roller.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as g]))

(s/def ::count pos-int?)
(s/def ::faces pos-int?)
(s/def ::roll int?)
(s/def ::rolls (s/coll-of ::roll))
(s/def ::best-of (s/nilable pos-int?))
(s/def ::worst-of (s/nilable pos-int?))
(s/def ::modifier (s/nilable int?))
(s/def ::expr-parts (s/cat :count ::count
                           :faces ::faces
                           :best-of ::best-of
                           :worst-of ::worst-of
                           :modifier ::modifier))

(def expr-re #"^(\d+?)d(\d+)(b(\d+?))?(w(\d+?))?([-+]\d+)?$")
;; ex: 1d20, 1d20+1, 2d20b1, 5d6w3

(defn make-expr [count faces best-of worst-of modifier]
  (let [base-str (format "%sd%s" count faces)
        best-of-str (if best-of
                      (format "b%s" best-of)
                      "")
        worst-of-str (if worst-of
                      (format "w%s" worst-of)
                      "")
        modifier-str (if modifier
                      (format "%s%s"
                              (if (< -1 modifier)
                                "+"
                                "")
                              modifier)
                      "")]
    (str base-str best-of-str worst-of-str modifier-str)))

(s/def ::die-expr
  (s/with-gen
    (s/and string? #(re-matches expr-re %))
    #(g/fmap
      (fn [args] (apply make-expr args))
      (s/gen ::expr-parts))))

(s/fdef make-expr
  :args ::expr-parts
  :ret ::die-expr)

(defn parse-expr [expr]
  (when-let [[_ count faces _ best-of _ worst-of modifier]
             (re-matches expr-re expr)]
    (vec
     (map
      #(when % (Integer/parseInt %))
      [count faces best-of worst-of modifier]))))

(s/fdef parse-expr
  :args (s/cat :expr string?)
  :ret (s/nilable ::expr-parts))

(defn roll-die [n]
  (+ 1 (rand-int n)))

(s/fdef roll-die
  :args (s/cat :n ::faces)
  :ret ::roll)

(defn eval-rolls [count faces best-of worst-of modifier]
  (->> (for [_ (range count)]
         (+ (or modifier 0)
            (roll-die faces)))
       sort
       (take-last (or best-of count))
       (take (or worst-of count))))

(s/fdef eval-rolls
  :args ::expr-parts
  :ret ::rolls)

(defn eval-die-expr [expr]
  (when-let [result (parse-expr expr)]
    (apply eval-rolls result)))

(s/fdef eval-die-expr
  :args (s/cat :expr string?)
  :ret (s/nilable ::rolls))
