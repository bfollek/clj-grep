(defproject clj-grep "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.flatland/ordered "1.5.7"]
                 [org.clojars.bfollek/rabbithole "0.2.2-SNAPSHOT"]]
  :repl-options {:init-ns clj-grep.core})
