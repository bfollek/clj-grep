(ns clj-grep.core-test
  (:require [clojure.test :refer :all]
            [clj-grep.core]))

(def original-file-names
  ["iliad.txt" "midsummer-night.txt" "paradise-lost.txt"])
(def file-names
  (conj original-file-names "same-line-repeats.txt"))

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
          {:file-names ["test/data/iliad.txt"]
           :options #clj_grep.core.Options
                     {:entire-lines true
                      :ignore-case false
                      :invert false
                      :line-numbers true
                      :only-names false}
           :pattern "foo"}
         (let [fun #'clj-grep.core/load-state]
           (fun "foo" "-x -n" ["test/data/iliad.txt"])))))

(deftest test-one-file-one-match-no-flags
  (is (= "Of Atreus, Agamemnon, King of men.\n"
         (clj-grep.core/grep "Agamemnon" "" ["test/data/iliad.txt"]))))

; def test_one_file_one_match_print_line_numbers_flag(self):
;         self.assertMultiLineEqual(
;             grep("Forbidden", "-n", [PARADISELOSTFILENAME]),
;             "2:Of that Forbidden Tree, whose mortal tast\n"
;         )

(deftest test-one-file-one-match-case_insensitive_flag
  (is (= "Of that Forbidden Tree, whose mortal tast\n"
         (clj-grep.core/grep "FORBIDDEN" "-i" ["test/data/paradise-lost.txt"]))))

(deftest test-one-file-one-match-print-file-names-flag
  (is (= "test/data/paradise-lost.txt\n"
         (clj-grep.core/grep "Forbidden", "-l", ["test/data/paradise-lost.txt"]))))

(deftest test-one-file-one-match-match-entire-lines-flag
  (is (= "With loss of Eden, till one greater Man\n"
         (clj-grep.core/grep "With loss of Eden, till one greater Man" "-x" ["test/data/paradise-lost.txt"]))))

;  def test_one_file_one_match_multiple_flags(self):
;         self.assertMultiLineEqual(
;             grep("OF ATREUS, Agamemnon, KIng of MEN.",
;                  "-n -i -x", [ILIADFILENAME]),
;             "9:Of Atreus, Agamemnon, King of men.\n")

(deftest test-one-file-several-matches-no-flags
  (is (= (str "Nor how it may concern my modesty,\n"
              "But I beseech your grace that I may know\n"
              "The worst that may befall me in this case,\n")
         (clj-grep.core/grep "may" "" ["test/data/midsummer-night.txt"]))))

    ; def test_one_file_several_matches_print_line_numbers_flag(self):
    ;     self.assertMultiLineEqual(
    ;         grep("may", "-n", [MIDSUMMERNIGHTFILENAME]),
    ;         "3:Nor how it may concern my modesty,\n"
    ;         "5:But I beseech your grace that I may know\n"
    ;         "6:The worst that may befall me in this case,\n")

(deftest test-one-file-several-matches-match-entire-lines-flag
  (is (= ""
         (clj-grep.core/grep "may" "-x" ["test/data/midsummer-night.txt"]))))

(deftest test-one-file-several-matches-case-insensitive-flag
  (is (= (str  "Achilles sing, O Goddess! Peleus' son;\n"
               "The noble Chief Achilles from the son\n")
         (clj-grep.core/grep "ACHILLES" "-i", ["test/data/iliad.txt"]))))

    ; def test_one_file_several_matches_inverted_flag(self):
    ;     self.assertMultiLineEqual(
    ;         grep("Of", "-v", [PARADISELOSTFILENAME]),
    ;         "Brought Death into the World, and all our woe,\n"
    ;         "With loss of Eden, till one greater Man\n"
    ;         "Restore us, and regain the blissful Seat,\n"
    ;         "Sing Heav'nly Muse, that on the secret top\n"
    ;         "That Shepherd, who first taught the chosen Seed\n")

    ; def test_one_file_one_match_file_flag_takes_precedence_over_line(self):
    ;     self.assertMultiLineEqual(
    ;         grep("ten",  "-n -l", [ILIADFILENAME]),
    ;         ILIADFILENAME + '\n')

;;;; LAST 2 TESTS

(deftest test-one-file-same-line-repeats-print-file_names-flag
  (is (= "test/data/same-line-repeats.txt\n"
         (clj-grep.core/grep "linerep" "-l" ["test/data/same-line-repeats.txt"]))))

(deftest test-one-file-same-line-repeats-no-flags
  (is (= (str "samelinerepeats\n"
              "samelinerepeats\n"
              "samelinerepeats\n")
         (clj-grep.core/grep "linerep" "" ["test/data/same-line-repeats.txt"]))))
