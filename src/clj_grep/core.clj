(ns clj-grep.core
  "grep"
  (:require [clojure.string :as str]
            [clojure.tools.cli :as cli]
            [flatland.ordered.set :as fls]
            [rabbithole.core :as rh]))

(defrecord Options [entire-lines ignore-case invert line-numbers only-names])
(defrecord State [pattern options file-names])

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
    (:ignore-case options) str/lower-case))

(defn- load-state
  [pattern flags file-names]
  (let [options (load-options flags)
        pattern (load-pattern pattern options)]
    (map->State {:pattern pattern :options options :file-names file-names})))

(defn- matches?
  [state line]
  (let [line (cond-> line (get-option state :ignore-case) str/lower-case)
        match? (str/includes? line (:pattern state))]
    (cond-> match? (get-option state :invert) not)))

(defn- fmt-line
  [line file-name options]
  (let [line (if (:only-names options) file-name line)]
    ; Add a newline
    (str line \newline)))

(defn- check-file
  [state file-name]
  (->> (rh/read-lines file-name)
       (filter #(matches? state %))
       (map #(fmt-line % file-name (:options state)))))

(defn run
  [state]
  (-> (map #(check-file state %) (:file-names state))
      flatten
      str/join))
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
  [pattern flags file-names]
  (-> (load-state pattern flags file-names)
      run))
