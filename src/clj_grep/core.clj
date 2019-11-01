(ns clj-grep.core
  "grep"
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.cli :as cli]))

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

(defn- number-lines
  "Transform a sequence of lines into a sequence of vectors.
  Each vector has a line number and a line."
  ; clj-grep.core=> (number-lines '("aaa" "bbb" "ccc"))
  ; ([1 "aaa"] [2 "bbb"] [3 "ccc"])
  [lines]
  (map-indexed (fn [idx elem] [(inc idx) elem]) lines))

(defn- matches?
  [state line]
  (let [txt (cond-> (second line) (get-option state :ignore-case) str/lower-case)
        match-func (if (get-option state :entire-lines) = str/includes?)
        match? (match-func txt (:pattern state))]
    (cond-> match? (get-option state :invert) not)))

(defn- fmt-line
  [line file-name state]
  (let [txt (second line)
        options (:options state)]
    (if
     (:only-names options)
      file-name
      (cond->> txt
        (:line-numbers options) (format "%d:%s" (first line))
        (> (count (:file-names state)) 1) (format "%s:%s" file-name)))))

(defn- fix-path
  [file-name]
  (str "test/data/" file-name))

(defn- check-file
  [state file-name]
  (with-open [rdr (io/reader (fix-path file-name))]
    (->> (line-seq rdr)
         number-lines
         (filter #(matches? state %))
         (map #(fmt-line % file-name state))
         doall)))

(defn- run
  [state]
  (-> (map #(check-file state %) (:file-names state))
      flatten
      ; If only file names, remove duplicates.
      (cond-> (get-option state :only-names) distinct)
      ; Add newlines.
      (interleave (repeat "\n"))
      ; Return one big string.
      str/join))

(defn grep
  "Lightweight grep based on an exercism.io python exercise."
  [pattern flags file-names]
  (-> (load-state pattern flags file-names)
      run))
