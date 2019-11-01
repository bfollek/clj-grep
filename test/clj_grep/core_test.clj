(ns clj-grep.core-test
  (:require [clojure.test :refer :all]
            [clj-grep.core]))

(def iliad "iliad.txt")
(def midsummer "midsummer-night.txt")
(def paradise "paradise-lost.txt")
(def same-line "same-line-repeats.txt")
(def original-file-names
  [iliad midsummer paradise])
(def file-names
  (conj original-file-names same-line))

(deftest test-load-options
  (is (=  #clj_grep.core.Options
           {:entire-lines false
            :ignore-case true
            :invert true
            :line-numbers false
            :only-names true}
          (let [fun #'clj-grep.core/load-options]
            (fun "-l -v -i")))))

(deftest test-load-state
  (is (= #clj_grep.core.State
          {:file-names ["iliad.txt"]
           :options #clj_grep.core.Options
                     {:entire-lines true
                      :ignore-case false
                      :invert false
                      :line-numbers true
                      :only-names false}
           :pattern "foo"}
         (let [fun #'clj-grep.core/load-state]
           (fun "foo" "-x -n" [iliad])))))

(deftest test-one-file-one-match-no-flags
  (is (= "Of Atreus, Agamemnon, King of men.\n"
         (clj-grep.core/grep "Agamemnon" "" [iliad]))))

(deftest test-one-file-one-match-print-line-numbers-flag
  (is (= "2:Of that Forbidden Tree, whose mortal tast\n"
         (clj-grep.core/grep "Forbidden" "-n" [paradise]))))

(deftest test-one-file-one-match-case-insensitive-flag
  (is (= "Of that Forbidden Tree, whose mortal tast\n"
         (clj-grep.core/grep "FORBIDDEN" "-i" [paradise]))))

(deftest test-one-file-one-match-print-file-names-flag
  (is (= "paradise-lost.txt\n"
         (clj-grep.core/grep "Forbidden" "-l" [paradise]))))

(deftest test-one-file-one-match-match-entire-lines-flag
  (is (= "With loss of Eden, till one greater Man\n"
         (clj-grep.core/grep "With loss of Eden, till one greater Man" "-x" [paradise]))))

(deftest test-one-file-one-match-multiple-flags
  (is (= "9:Of Atreus, Agamemnon, King of men.\n"
         (clj-grep.core/grep "OF ATREUS, Agamemnon, KIng of MEN." "-n -i -x" [iliad]))))

(deftest test-one-file-several-matches-no-flags
  (is (= (str "Nor how it may concern my modesty,\n"
              "But I beseech your grace that I may know\n"
              "The worst that may befall me in this case,\n")
         (clj-grep.core/grep "may" "" [midsummer]))))

(deftest test-one-file-several-matches-print-line-numbers-flag
  (is (= (str "3:Nor how it may concern my modesty,\n"
              "5:But I beseech your grace that I may know\n"
              "6:The worst that may befall me in this case,\n")
         (clj-grep.core/grep "may" "-n" [midsummer]))))

(deftest test-one-file-several-matches-match-entire-lines-flag
  (is (= ""
         (clj-grep.core/grep "may" "-x" [midsummer]))))

(deftest test-one-file-several-matches-case-insensitive-flag
  (is (= (str  "Achilles sing, O Goddess! Peleus' son;\n"
               "The noble Chief Achilles from the son\n")
         (clj-grep.core/grep "ACHILLES" "-i" [iliad]))))

(deftest test-one-file-several-matches-inverted-flag
  (is (= (str  "Brought Death into the World, and all our woe,\n"
               "With loss of Eden, till one greater Man\n"
               "Restore us, and regain the blissful Seat,\n"
               "Sing Heav'nly Muse, that on the secret top\n"
               "That Shepherd, who first taught the chosen Seed\n")
         (clj-grep.core/grep "Of" "-v" [paradise]))))

(deftest test-one-file-one-match-file-flag-takes-precedence-over-line
  (is (= (str iliad "\n")
         (clj-grep.core/grep "ten" "-n -l" [iliad]))))

(deftest test-one-file-no-matches-various-flags
  (is (= "" (clj-grep.core/grep "Gandalf" "-n -l -x -i" [iliad]))))

(deftest test-multiple-files-one-match-no-flags
  (is (= "iliad.txt:Of Atreus, Agamemnon, King of men.\n"
         (clj-grep.core/grep "Agamemnon" "" original-file-names))))

(deftest test-multiple-files-several-matches-no-flags
  (is (= (str  "midsummer-night.txt:Nor how it may concern my modesty,\n"
               "midsummer-night.txt:But I beseech your grace that I may know\n"
               "midsummer-night.txt:The worst that may befall me in this case,\n")
         (clj-grep.core/grep "may" "" original-file-names))))

(deftest test-multiple-files-several-matches-print-line-numbers-flag
  (is (= (str "midsummer-night.txt:5:But I beseech your grace that I may know\n"
              "midsummer-night.txt:6:The worst that may befall me in this case,\n"
              "paradise-lost.txt:2:Of that Forbidden Tree, whose mortal tast\n"
              "paradise-lost.txt:6:Sing Heav'nly Muse, that on the secret top\n")
         (clj-grep.core/grep "that" "-n" original-file-names))))

(deftest test-multiple-files-one-match-print-file-names-flag
  (is (= (str iliad "\n" paradise "\n")
         (clj-grep.core/grep "who" "-l" original-file-names))))

(deftest test-multiple-files-several-matches-case-insensitive-flag
  (is (= (str
          "iliad.txt:Caused to Achaia's host, sent many a soul\n"
          "iliad.txt:Illustrious into Ades premature,\n"
          "iliad.txt:And Heroes gave (so stood the will of Jove)\n"
          "iliad.txt:To dogs and to all ravening fowls a prey,\n"
          "midsummer-night.txt:I do entreat your grace to pardon me.\n"
          "midsummer-night.txt:In such a presence here to plead my thoughts;"
          "\nmidsummer-night.txt:If I refuse to wed Demetrius.\n"
          "paradise-lost.txt:Brought Death into the World, and all our woe,"
          "\nparadise-lost.txt:Restore us, and regain the blissful Seat,\n"
          "paradise-lost.txt:Sing Heav'nly Muse, that on the secret top\n")
         (clj-grep.core/grep "TO" "-i" original-file-names))))

(deftest test-multiple-files-several-matches-inverted-flag
  (is (= (str "iliad.txt:Achilles sing, O Goddess! Peleus' son;\n"
              "iliad.txt:The noble Chief Achilles from the son\n"
              "midsummer-night.txt:If I refuse to wed Demetrius.\n")
         (clj-grep.core/grep "a" "-v" original-file-names))))

(deftest test-multiple-files-one-match-match-entire-lines-flag
  (is (=  "midsummer-night.txt:But I beseech your grace that I may know\n"
          (clj-grep.core/grep "But I beseech your grace that I may know" "-x"
                              original-file-names))))

(deftest test-multiple-files-one-match-multiple-flags
  (is (= "paradise-lost.txt:4:With loss of Eden, till one greater Man\n"
         (clj-grep.core/grep "WITH LOSS OF EDEN, TILL ONE GREATER MAN"  "-n -i -x" original-file-names))))

(deftest test-multiple-files-no-matches-various-flags
  (is (= "" (clj-grep.core/grep "Frodo" "-n -l -x -i" original-file-names))))

(deftest test-multiple-files-several-matches-file-flag-takes-precedence
  (is (= (str iliad "\n" paradise "\n")
         (clj-grep.core/grep "who" "-n -l" original-file-names))))

(deftest test-multiple-files-several-matches-inverted-match-entire-lines
  (is (= (str "iliad.txt:Achilles sing, O Goddess! Peleus' son;\n"
              "iliad.txt:His wrath pernicious, who ten thousand woes\n"
              "iliad.txt:Caused to Achaia's host, sent many a soul\n"
              "iliad.txt:And Heroes gave (so stood the will of Jove)\n"
              "iliad.txt:To dogs and to all ravening fowls a prey,\n"
              "iliad.txt:When fierce dispute had separated once\n"
              "iliad.txt:The noble Chief Achilles from the son\n"
              "iliad.txt:Of Atreus, Agamemnon, King of men.\n"
              "midsummer-night.txt:I do entreat your grace to pardon me.\n"
              "midsummer-night.txt:I know not by what power I am made bold,\n"
              "midsummer-night.txt:Nor how it may concern my modesty,\n"
              "midsummer-night.txt:In such a presence here to plead my thoughts;"
              "\nmidsummer-night.txt:But I beseech your grace that I may know\n"
              "midsummer-night.txt:The worst that may befall me in this case,\n"
              "midsummer-night.txt:If I refuse to wed Demetrius.\n"
              "paradise-lost.txt:Of Mans First Disobedience, and the Fruit\n"
              "paradise-lost.txt:Of that Forbidden Tree, whose mortal tast\n"
              "paradise-lost.txt:Brought Death into the World, and all our woe,"
              "\nparadise-lost.txt:With loss of Eden, till one greater Man\n"
              "paradise-lost.txt:Restore us, and regain the blissful Seat,\n"
              "paradise-lost.txt:Sing Heav'nly Muse, that on the secret top\n"
              "paradise-lost.txt:Of Oreb, or of Sinai, didst inspire\n"
              "paradise-lost.txt:That Shepherd, who first taught the chosen Seed"
              "\n")
         (clj-grep.core/grep "Illustrious into Ades premature," "-x -v"
                             original-file-names))))

(deftest test-one-file-same-line-repeats-print-file-names-flag
  (is (= "same-line-repeats.txt\n"
         (clj-grep.core/grep "linerep" "-l" [same-line]))))

(deftest test-one-file-same-line-repeats-no-flags
  (is (= (str "samelinerepeats\n"
              "samelinerepeats\n"
              "samelinerepeats\n")
         (clj-grep.core/grep "linerep" "" [same-line]))))
