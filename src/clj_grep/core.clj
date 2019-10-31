(ns clj-grep.core
  "grep"
  (:require [clojure.string :as str]
            [clojure.tools.cli :as cli]
            [rabbithole.core :as rh]))

(defrecord Options [entire-lines ignore-case invert line-numbers only-names])
(defrecord State [pattern options file-names])

(def ^:private cli-options
  ; The long form becomes the keyword.
  ; :default false is necessary to set missing options.
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
      ;; Pull out the piece of the map we care about.
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
        match-func (if (get-option state :entire-lines) = str/includes?)
        match? (match-func line (:pattern state))]
    (cond-> match? (get-option state :invert) not)))

(defn- fmt-line
  [line file-name state]
  (let [options (:options state)]
    (cond
      (:only-names options) file-name
      (> (count (:file-names state)) 1) (format "%s:%s" file-name line)
      :else line)))

(defn- fix-path
  [file-name]
  (str "test/data/" file-name))

(defn- check-file
  [state file-name]
  (->> (rh/read-lines (fix-path file-name))
       (filter #(matches? state %))
       (map #(fmt-line % file-name state))))

(defn run
  [state]
  (-> (map #(check-file state %) (:file-names state))
      flatten
      ; If only file names, remove duplicates.
      (cond-> (get-option state :only-names) distinct)
      ; Add a newlines
      (interleave (repeat "\n"))
      ; Return one big string
      str/join))

(defn grep
  "Lightweight grep based on an exercism.io python exercise."
  [pattern flags file-names]
  (-> (load-state pattern flags file-names)
      run))
