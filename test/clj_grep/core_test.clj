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
          {:files ["test/data/iliad.txt"]
           :options #clj_grep.core.Options
                     {:entire-lines true
                      :ignore-case false
                      :invert false
                      :line-numbers true
                      :only-names false}
           :pattern "foo\n"}
         (let [fun #'clj-grep.core/load-state]
           (fun "foo" "-x -n" ["test/data/iliad.txt"])))))
