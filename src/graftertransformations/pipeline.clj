(ns graftertransformations.pipeline
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
     [grafter.vocabularies.vcard :refer :all]
     [graftertransformations.prefix :refer :all]
     [graftertransformations.transform :refer :all]))

;; Declare our graphs templates which will destructure each row and
;; convert it into an RDF graphs. This will be the final step in our
;; pipeline definition.

;; AIR QUALITY
(def make-air-quality-graph
    (graph-fn [{:keys [Name Description Province Town Address CoordenatesX CoordenatesY Latitude Longitude quality-uri] :as row}]
        (graph (base-graph "Air Quality Basque Country 2017")
            [quality-uri
                [qb:Observation]
                ;;[qb:attribute (langSp Name)]
                ["http://schema.org/addressRegion" (langVq Province)]
                [vcard:locality (langSp Town)]
                [vcard:street-address (langSp Address)]
                [qb:measureType CoordenatesX]
                [qb:measureType CoordenatesY]
                ["http://www.w3.org/2003/01/geo/wgs84_pos#lat" Latitude]
                ["http://www.w3.org/2003/01/geo/wgs84_pos#long" Longitude]
            ]
        )
    )
)

;; CELICA
(def make-celica-graph
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

;; PEOPLE
(def make-people-graph
  (graph-fn [{:keys [name surname nick sex age email web married city nationality birthPlace work car gender marriage person-uri info-uri] :as row}]
            (graph (base-graph "People")
                  [person-uri
                      [rdf:a foaf:Person]
                      [rdfs:comment (commentary (str "This description is about " name " " surname))]
                      [foaf:name (s name)]
                      [base-surname (s surname)]
                      [foaf:nick (s nick)]
                      [foaf:age (integer age)]
                      ;[foaf:mbox (s mail)]
                      ;[foaf:mbox_sha1sum mail]
                      [foaf:homepage (s web)]
                      [base-email (s email)]
                      [foaf:gender sex]
                      [base-marriage married]
                      [vcard:locality (s city)]
                      ["http://dbpedia.org/ontology/nationality" (s nationality)]
                      ["http://dbpedia.org/ontology/birthPlace" (s birthPlace)]
                      ["http://dbpedia.org/ontology/Work" (s work)]
                      ["http://dbpedia.org/resource/Car" (s car)]
                      [foaf:knows "http://dbpedia.org/resource/Bob_Marley"]
                  ]
                  [info-uri
                      [rdfs:comment (commentary (str "All the data of this file has been created randomly"))]
                  ]
            )
   )
)

;; Declare pipes so the plugin can find and run them. It's just a
;; function from Datasetable -> Dataset.

;; AIR QUALITY
(defn convert-air-quality-to-data
  "Pipeline to convert tabular people data into a different tabular format."
  [data-file]
  (-> (read-dataset data-file)
      (make-dataset move-first-row-to-header)
      (make-dataset [:Name :Description :Province :Town :Address :CoordenatesX :CoordenatesY :Latitude :Longitude])
      (mapc {
               :Latitude  parseValue
               :Longitude parseValue
               :CoordenatesX  parseValue
               :CoordenatesY parseValue
               :Address removeSymbols
            }
      )
      (derive-column :quality-uri [:Name] quality-uri)
  )
)

(defn convert-air-quality-data-to-graph
  "Pipeline to convert the tabular people data sheet into graph data."
  [dataset]
  (-> dataset convert-air-quality-to-data make-air-quality-graph missing-data-filter))

(declare-pipeline convert-air-quality-to-data [Dataset -> Dataset]
                  {data-file "A data file"})

(declare-pipeline convert-air-quality-data-to-graph [Dataset -> (Seq Statement)]
                  {dataset "The data file to convert into a graph."})

;; CELICA
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

(defn convert-celica-data-to-graph
  "Pipeline to convert the tabular Celica data sheet into graph data."
  [dataset]
  (-> dataset convert-celica-to-data make-celica-graph missing-data-filter))

(declare-pipeline convert-celica-to-data [Dataset -> Dataset]
                  {data-file "A data file"})

(declare-pipeline convert-celica-data-to-graph [Dataset -> (Seq Statement)]
                  {dataset "The data file to convert into a graph."})

;; PEOPLE
(defn convert-people-to-data
  "Pipeline to convert tabular people data into a different tabular format."
  [data-file]
  (-> (read-dataset data-file)
      (make-dataset move-first-row-to-header)
      (make-dataset [:name :surname :nick :sex :age :email :web
        :married :city :nationality :birthPlace :work :car])
      (mapc {:sex {"f" (s "Female")
                   "m" (s "Male")}
             :married {"y" (s "Yes")
                       "n" (s "No")}})
      (derive-column :person-uri [:name :surname] person-uri)
      (derive-column :info-uri ["Info"] info-uri)
  )
)

(defn convert-people-data-to-graph
  "Pipeline to convert the tabular people data sheet into graph data."
  [dataset]
  (-> dataset convert-people-to-data make-people-graph missing-data-filter))

(declare-pipeline convert-people-to-data [Dataset -> Dataset]
                  {data-file "A data file"})

(declare-pipeline convert-people-data-to-graph [Dataset -> (Seq Statement)]
                  {dataset "The data file to convert into a graph."})
