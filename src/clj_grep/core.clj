(ns clj-grep.core
  "grep"
  (:require [clojure.string :as str]
            [clojure.tools.cli :as cli]
            [flatland.ordered.set :as fls]
            [rabbithole.core :as rh]))

(defrecord Options [entire-lines ignore-case invert line-numbers only-names])
(defrecord State [pattern options files])

(def ^:private cli-options
  ;; The long form becomes the keyword.
  ;; :default false is necessary to set missing options.
  [["-x" "--entire-lines" :default false]
   ["-i" "--ignore-case" :default false]
   ["-v" "--invert" :default false]
   ["-n" "--line-numbers" :default false]
   ["-l" "--only-names" :default false]])

(defn- load-options
  [flags]
  (-> flags
      (str/split #" ")
      (cli/parse-opts cli-options)
      ; Pull out the piece of the map we care about.
      :options
      map->Options))

(defn- get-option
  [state k]
  (get-in state [:options k]))

(defn- load-pattern
  [pattern options]
  (cond-> pattern
    (:ignore-case options) str/lower-case
    ; Lines have trailing newlines, so add a newline to the pattern.
    (:entire-lines options) (str \newline)))

(defn- load-state
  [pattern flags files]
  (let [options (load-options flags)
        pattern (load-pattern pattern options)]
    (map->State {:pattern pattern :options options :files files})))

(defn- matches?
  [state line]
  (let [line (if (get-option state :ignore-case) (str/lower-case line) line)
        match? (str/includes? line (:pattern state))]
    (if (get-option state :invert) (not match?) match?)))

(defn- fmt-line
  [line file-name options]
  ; Add a newline
  (str line "\n"))

(defn- check-file
  [state file]
  (let [lines (rh/read-lines file)]
    (map #(fmt-line % file (:options state)) (filter #(matches? state %) lines))))

(defn run
  [state]
  (-> (map #(check-file state %) (:files state))
      flatten
      str/join))

      ;#(str/join "\n" %)))
      ;(str "\n"))) ; Trailing newline
   ;; TODO Handle newlines with each line, upstairs, when I (format-line)
   ;; TODO dedup if only_names
   ;; (into fls/ordered-set)))) ; Get rid of dups
   ;; TODO break out of seqs

; def _run(state: _State) -> str:
;     results = {} # dict gives us ordered keys, no dups
;     for file_name in state.files:
;         with open(file_name) as f:
;             cnt = 0
;             # Doesn't work. (Because of the doctored File object they provide?)
;             # for line in f:
;             lines = f.readlines()
;             for line in lines:
;                 cnt += 1
;                 match = _matches(state, line)
;                 result = _calc_result(state, match, line, cnt, file_name)
;                 if result:
;                     results[result] = True
;     return ''.join(list(results)) # list(dict) gets the keys

(defn grep
  "Lightweight grep based on an exercism.io python exercise."
  [pattern flags files]
  (let [state (load-state pattern flags files)]
    (run state)))
