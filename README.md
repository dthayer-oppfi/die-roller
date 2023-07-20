# die-roller

A die roller written in Clojure. It provides a `do-expr` function that parses a string "die expression" into the results of rolling one or more virtual dice. These die expressions include:

- 1d20: roll one twenty-sided die.
- 1d6+1: roll one six-sided die and add one.
- 4d6b3: roll four six-sided dice and keep the highest three rolls.
- 4d6w3: roll four six-sided dice and keep the lowest three rolls.

Invalid expressions will return `nil`.

## Usage

This code is not published anywhere.
Good luck getting it as a dependency!

```clojure
(ns your-ns
  (:require [die-roller.core :refer [do-expr]]))

(println (do-expr "1d20"))
;; => (19) ; a list of results, specifically rolling one d20.

;; tip: use `do-rolls` to feed inputs directly to the die roller.
(let [count 1
      faces 20
      best-of nil
      worst-of nil
      modifier nil]
  (println (do-rolls count faces best-of worst-of modifier)))
;; => (2) ; you fail to persuade the king that his shoes are untied.
```

## License

Copyright © 2023 Diana Thayer

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
