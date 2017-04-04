(ns celica.pipeline
    (:require
     [grafter.tabular :refer [_ add-column add-columns apply-columns
                              build-lookup-table column-names columns
                              derive-column drop-rows graph-fn grep make-dataset
                              mapc melt move-first-row-to-header read-dataset
                              read-datasets rows swap swap take-rows
                              test-dataset test-dataset]]
     [grafter.rdf]
     [grafter.rdf.io]
     [grafter.rdf :refer [s]]
     [grafter.rdf.protocols :refer [->Quad]]
     [grafter.rdf.protocols :refer [ITripleWriteable]]
     [grafter.rdf.templater :refer [graph]]
     [grafter.rdf.io :refer [rdf-serializer]]
     [grafter.rdf.formats :refer [rdf-nquads rdf-turtle]]
     [grafter.pipeline :refer [declare-pipeline]]
     [grafter.vocabularies.qb :refer :all]
     [grafter.vocabularies.rdf :refer :all]
     [grafter.vocabularies.foaf :refer :all]
     [celica.prefix :refer :all]
     [celica.transform :refer :all]))

;; Declare our graph template which will destructure each row and
;; convert it into an RDF graph.  This will be the final step in our
;; pipeline definition.

(def make-graph
    (graph-fn [{:keys [brand name generation startedProduction finishedProduction
                       platform engineCode version engineSize valves maxTorque traction length width height curbWeight fuelType fuelTank
                       numberAirbags numberDoors price celica-uri data-uri] :as row}]
              (graph (base-graph "Celica")
                    [celica-uri
                        [rdf:a "http://dbpedia.org/resource/Car"]
                        [rdfs:comment (commentary (str "This info is about the " brand " " name " " generation " gen, which engine version is " version))]
                        [car-manufacturer (s brand)]
                        [car-name (s name)]
                        [start-manufacture (integer startedProduction)]
                        [end-manufacture (integer finishedProduction)]
                        [platform-types (s platform)]
                        [engine-code (s engineCode)]
                        [car-version (s version)]
                        [engine-size (s (add-cubic-centimetre engineSize))]
                        [engine-valves (s valves)]
                        [engine-maxTorque (s (add-revolutions-per-minute maxTorque))]
                        [car-traction (s traction)]
                        [measurements-length (s (add-millimeters length))]
                        [measurements-width (s (add-millimeters width))]
                        [measurements-height (s (add-millimeters height))]
                        [measurements-curbWeight (s (add-kilograms curbWeight))]
                        [car-fuel fuelType]
                        [car-fuel-tank (s (add-litres fuelTank))]
                        [car-airbags (integer numberAirbags)]
                        [car-doors (integer numberDoors)]
                        [car-price (s (add-euro-symbol price))]
                    ]
                    [data-uri
                        [rdfs:comment (commentary (str "All the data references are taken from one model, even though, there are more") )]
                    ]
              )
    )
)


;; Declare a pipe so the plugin can find and run it.  It's just a
;; function from Datasetable -> Dataset.
(defn convert-celica-to-data
  "Pipeline to convert tabular celica data into a different tabular format."
  [data-file]
  (-> (read-dataset data-file)
      (make-dataset move-first-row-to-header)
      (make-dataset [:brand :name :generation :startedProduction :finishedProduction
                     :platform :engineCode :version :engineSize :valves :maxTorque :traction
                     :length :width :height :curbWeight :fuelType :fuelTank
                     :numberAirbags :numberDoors :price])
      (mapc {:fuelType {"p" (s "Petrol")
                        "d" (s "Diesel")
                       }
            }
      )
      (derive-column :celica-uri [:brand :name :generation :version] celica-uri)
      (derive-column :data-uri [:brand :name] data-uri)
  )
)

(declare-pipeline convert-celica-to-data [Dataset -> Dataset]
                  {data-file "A data file"})

(defn convert-celica-data-to-graph
  "Pipeline to convert the tabular Celica data sheet into graph data."
  [dataset]
  (-> dataset convert-celica-to-data make-graph missing-data-filter))

(declare-pipeline convert-celica-data-to-graph [Dataset -> (Seq Statement)]
                  {dataset "The data file to convert into a graph."})
