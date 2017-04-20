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
    (graph-fn [{:keys [manufacturer model generation productionStartYear productionEndYear
                       platform engineCode engine displacement valves torqueOutput traction length width height curbWeight fuelType fuelTank
                       numberAirbags numberDoors price celica-uri celica-displacement-extended-uri celica-torqueOutput-extended-uri
                       celica-dimensions-extended-uri celica-curbWeight-extended-uri celica-fuelCapacity-extended-uri
                       celica-price-extended-uri data-uri] :as row}]
              (graph (base-graph "Celica")
                    [celica-uri
                        [rdf:a "http://dbpedia.org/resource/Car"]
                        [rdfs:comment (commentary (str "This info is about the " manufacturer " " model " " generation " gen, which engine version is " engine))]
                        ["http://dbpedia.org/ontology/manufacturer" (s manufacturer)]
                        ["http://dbpedia.org/resource/Car_model" (s model)]
                        ["http://dbpedia.org/ontology/productionStartYear" (integer productionStartYear)]
                        ["http://dbpedia.org/ontology/productionEndYear" (integer productionEndYear)]
                        [platform-types (s platform)]
                        [engine-code (s engineCode)]
                        ["http://dbpedia.org/property/engine" (s engine)]
                        [engine-displacement celica-displacement-extended-uri]
                        ["http://dbpedia.org/property/engineValve" (s valves)]
                        [engine-torque celica-torqueOutput-extended-uri]
                        [car-traction (s traction)]
                        [measurements-size celica-dimensions-extended-uri]
                        [measurements-curbWeight celica-curbWeight-extended-uri]
                        ["http://dbpedia.org/ontology/fuelType" fuelType]
                        [car-fuel-tank celica-fuelCapacity-extended-uri]
                        ["http://dbpedia.org/resource/Airbag" (integer numberAirbags)]
                        ["http://dbpedia.org/resource/Cardoor" (integer numberDoors)]
                        [car-price celica-price-extended-uri]
                    ]
                    [celica-displacement-extended-uri
                        ["http://dbpedia.org/ontology/Engine/displacement" displacement]
                        [qb:measureType "http://dbpedia.org/resource/Cubic_centimetre"]
                    ]
                    [celica-torqueOutput-extended-uri
                        ["http://dbpedia.org/ontology/Engine/torqueOutput" torqueOutput]
                        [qb:measureType "http://dbpedia.org/resource/Revolutions_per_minute"]
                    ]
                    [celica-dimensions-extended-uri
                        ["http://live.dbpedia.org/ontology/MeanOfTransportation/length" length]
                        ["http://live.dbpedia.org/ontology/MeanOfTransportation/width" width]
                        ["http://live.dbpedia.org/ontology/MeanOfTransportation/height" height]
                        [qb:measureType "http://dbpedia.org/resource/Millimetre"]
                    ]
                    [celica-curbWeight-extended-uri
                        ["http://dbpedia.org/property/curbWeight" curbWeight]
                        [qb:measureType "http://dbpedia.org/resource/Kilogram"]
                    ]
                    [celica-fuelCapacity-extended-uri
                        ["http://dbpedia.org/ontology/fuelCapacity" fuelTank]
                        [qb:measureType "http://dbpedia.org/resource/Litre"]
                    ]
                    [celica-price-extended-uri
                        ["http://dbpedia.org/ontology/price" price]
                        ["http://dbpedia.org/ontology/Currency" "http://dbpedia.org/resource/Euro"]
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

;; WORK CALENDAR 2017
(def make-work-calendar-2017-graph
    (graph-fn [{:keys [Fecha DescripcionES DescripcionEU LugarES LugarEU Territorio CodigoEustat Latitud Longitud calendar-uri] :as row}]
        (graph (base-graph "Work Calendar of Basque Country 2017")
            [calendar-uri
                [qb:Observation]
                ["http://purl.org/dc/terms/date" (s Fecha)]
                ["http://purl.org/dc/terms/description" (langSp DescripcionES)]
                ["http://purl.org/dc/terms/description" (langVq DescripcionEU)]
                [vcard:locality (langSp LugarES)]
                [vcard:locality (langVq LugarEU)]
                ["http://dbpedia.org/ontology/Territory" (s Territorio)]
                [base-eustatCode CodigoEustat]
                ["http://www.w3.org/2003/01/geo/wgs84_pos#lat" Latitud]
                ["http://www.w3.org/2003/01/geo/wgs84_pos#long" Longitud]
            ]
        )
    )
)

;; Declare pipes so the plugin can find and run them. It's just a
;; function from Datasetable -> Dataset.

;; AIR QUALITY
(defn convert-air-quality-to-data
  "Pipeline to convert tabular air quality data into a different tabular format."
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
  "Pipeline to convert the tabular air quality data sheet into graph data."
  [dataset]
  (-> dataset convert-air-quality-to-data make-air-quality-graph missing-data-filter))

(declare-pipeline convert-air-quality-to-data [Dataset -> Dataset]
                  {data-file "A data file"})

(declare-pipeline convert-air-quality-data-to-graph [Dataset -> (Seq Statement)]
                  {dataset "The data file to convert into a graph."})

;; CELICA
(defn convert-celica-to-data
  "Pipeline to convert tabular Celica data into a different tabular format."
  [data-file]
  (-> (read-dataset data-file)
      (make-dataset move-first-row-to-header)
      (make-dataset [:manufacturer :model :generation :productionStartYear :productionEndYear
                     :platform :engineCode :engine :displacement :valves :torqueOutput :traction
                     :length :width :height :curbWeight :fuelType :fuelTank
                     :numberAirbags :numberDoors :price])
      (mapc {:fuelType {"p" (s "Petrol")
                        "d" (s "Diesel")
                       }
             :displacement parseValue
             :torqueOutput parseValue
             :length parseValue
             :width parseValue
             :height parseValue
             :curbWeight parseValue
             :fuelTank parseValue
             :price parseValue
            }
      )
      (derive-column :celica-uri [:manufacturer :model :generation :engine] celica-uri)
      (derive-column :celica-displacement-extended-uri [:manufacturer :model :generation :engine :displacement] celica-displacement-extended-uri)
      (derive-column :celica-torqueOutput-extended-uri [:manufacturer :model :generation :engine :torqueOutput] celica-torqueOutput-extended-uri)
      (derive-column :celica-dimensions-extended-uri [:manufacturer :model :generation :engine :length :width :height] celica-dimensions-extended-uri)
      (derive-column :celica-curbWeight-extended-uri [:manufacturer :model :generation :engine :curbWeight] celica-curbWeight-extended-uri)
      (derive-column :celica-fuelCapacity-extended-uri [:manufacturer :model :generation :engine :fuelTank] celica-fuelCapacity-extended-uri)
      (derive-column :celica-price-extended-uri [:manufacturer :model :generation :engine :price] celica-price-extended-uri)
      (derive-column :data-uri [:manufacturer :model] data-uri)
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

;; WORK CALENDAR 2017
(defn convert-work-calendar-2017-to-data
  "Pipeline to convert tabular work calendar data into a different tabular format."
  [data-file]
  (-> (read-dataset data-file)
      (make-dataset move-first-row-to-header)
      (make-dataset [:Fecha :DescripcionES :DescripcionEU :LugarES :LugarEU
                     :Territorio :CodigoEustat :Latitud :Longitud])
      (mapc {
               :Fecha organizeDate
               :DescripcionES adaptDescriptions
               :DescripcionEU adaptDescriptions
               :LugarES replaceDashes
               :LugarEU replaceDashes
               :Territorio replaceDashes
               :CodigoEustat parseValue
               :Latitud parseValue
               :Longitud parseValue
            }
      )
      (derive-column :calendar-uri [:DescripcionEU] calendar-uri)
  )
)

(defn convert-work-calendar-2017-data-to-graph
  "Pipeline to convert the tabular work calendar data sheet into graph data."
  [dataset]
  (-> dataset convert-work-calendar-2017-to-data make-work-calendar-2017-graph missing-data-filter))

(declare-pipeline convert-work-calendar-2017-to-data [Dataset -> Dataset]
                  {data-file "A data file"})

(declare-pipeline convert-work-calendar-2017-data-to-graph [Dataset -> (Seq Statement)]
                  {dataset "The data file to convert into a graph."})
