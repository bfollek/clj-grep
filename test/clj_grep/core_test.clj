(ns clj-grep.core-test
  (:require [clojure.test :refer :all]
            [clj-grep.core :refer :all]))

(deftest test-load-state
  (is (= {:files ["mike.txt"]
          :options {:entire-lines true
                    :ignore-case false
                    :invert false
                    :line-numbers true
                    :only-names false}
          :pattern "foo\n"} (load-state "foo" "-x -n" ["mike.txt"]))))
