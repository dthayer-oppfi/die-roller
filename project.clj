(defproject die-roller "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :plugins [[lein-cloverage "1.2.2"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "1.1.1"]
                                  [orchestra "2021.01.01-1"]]}}
  :repl-options {:init-ns die-roller.core})
