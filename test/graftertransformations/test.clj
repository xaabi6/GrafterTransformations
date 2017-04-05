(ns graftertransformations.test
  (:require
   [clojure.test :refer :all]
   [graftertransformations.pipeline :refer :all]
  )
)

(deftest test-celica-brand
  (testing "Value of brand column for row 1 should be Toyota"
     (is (= "Toyota"
            ; There is probably a better solution to achieve this
            (get (nth (:rows (convert-celica-to-data "./data/celica-data.csv")) 1) "brand" )
         )
     )
  )
)
