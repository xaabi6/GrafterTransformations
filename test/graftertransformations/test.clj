(ns graftertransformations.test
  (:require
   [clojure.test :refer :all]
   [grafter.rdf.repository :refer :all]
   [graftertransformations.pipeline :refer :all]
  )
)

(deftest test-celica-brand
  (testing "Value of brand column for row 0 should be Toyota"
     (is (= "Toyota"
            ; There is probably a better solution to achieve this
            (get (nth (:rows (convert-celica-to-data "./data/celica-data.csv")) 0) :brand )
         )
     )
  )
)

(deftest test-celica-name
  (testing "Value of name column for row 1 should be Celica"
     (is (= "Celica"
            ; There is probably a better solution to achieve this
            (get (nth (:rows (convert-celica-to-data "./data/celica-data.csv")) 1) :name )
         )
     )
  )
)

(deftest test-data-quality
  (testing "Query data"
     (query sesame-store "SELECT * WHERE { ?s ?p ?o } LIMIT 10")
  )
)

(def sesame-store
  (repo (native-store "/tmp/sesame-db"))
)
