(ns clj-grep.core-test
  (:require [clojure.test :refer :all]
            [clj-grep.core]))

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