(ns clj-grep.core
  "grep"
  (:require [clojure.string :as string]
            [clojure.tools.cli :as cli]
            [rabbithole.core :as rh]))

(def cli-options
  ;; The long form becomes the keyword.
  ;; :default false is necessary to set missing options.
  [["-x" "--entire-lines" :default false]
   ["-i" "--ignore-case" :default false]
   ["-v" "--invert" :default false]
   ["-n" "--line-numbers" :default false]
   ["-l" "--only-names" :default false]])

(defn load-options
  [flags]
  (-> flags
      (string/split #" ")
      (cli/parse-opts cli-options)
      ; Pull out the piece of the map we care about.
      :options))

(defn load-pattern
  [pattern options]
  (cond-> pattern
    (:ignore-case options) string/lower-case
    ; Lines have trailing newlines, so add a newline to the pattern.
    (:entire-lines options) (str \newline)))

(defn load-state
  [pattern flags files]
  (let [options (load-options flags)
        pattern (load-pattern pattern options)]
    {:pattern pattern :options options :files files}))

(defn grep
  "Lightweight grep based on an exercism.io python exercise."
  [pattern flags files]
  (let [state (load-state pattern flags files)]
    (println state)))
