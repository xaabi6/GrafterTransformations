(ns graftertransformations.test
  (:require
   [clojure.test :refer :all]
   [grafter.rdf.repository :refer :all]
   [graftertransformations.pipeline :refer :all]
  )
)

(deftest test-celica-manufacturer
  (testing "Value of manufacturer column for row 0 should be Toyota"
     (is (= "Toyota"
            ; There is probably a better solution to achieve this
            (get (nth (:rows (convert-celica-to-data "./data/celica-data.csv")) 0) :manufacturer )
         )
     )
  )
)

(deftest test-celica-model
  (testing "Value of model column for row 1 should be Celica"
     (is (= "Celica"
            ; There is probably a better solution to achieve this
            (get (nth (:rows (convert-celica-to-data "./data/celica-data.csv")) 1) :model )
         )
     )
  )
)
