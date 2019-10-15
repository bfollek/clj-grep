(ns clj-grep.core
  "grep"
  (:require [clojure.string :as string]
            [rabbithole.core :as rh]))

; TODO Some way to map across coll of keywords, coll of flag values
; use includes?, then (into) a map the keywords and bools?
(defn load-options
  [flags]
  {:entire-lines (string/includes? flags "-x")
   :ignore-case (string/includes? flags "-i")
   :invert (string/includes? flags "-v")
   :line-numbers (string/includes? flags "-n")
   :only-names (string/includes? flags "-l")})


; TODO This is ugly. Can I thread it?
(defn load-pattern
  [pattern options]
  (let [pattern (if (:ignore-case options)
                  (string/lower-case pattern)
                  pattern)
        pattern (if (:entire-lines options)
                  (str pattern \newline) ; lines have trailing newlines
                  pattern)]
    pattern))

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
