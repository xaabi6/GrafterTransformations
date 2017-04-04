(ns people.pipeline
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
     [people.prefix :refer :all]
     [people.transform :refer :all]))

;; Declare our graph template which will destructure each row and
;; convert it into an RDF graph. This will be the final step in our
;; pipeline definition.

(def make-graph
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


;; Declare a pipe so the plugin can find and run it. It's just a
;; function from Datasetable -> Dataset.
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

(declare-pipeline convert-people-to-data [Dataset -> Dataset]
                  {data-file "A data file"})

(defn convert-people-data-to-graph
  "Pipeline to convert the tabular people data sheet into graph data."
  [dataset]
  (-> dataset convert-people-to-data make-graph missing-data-filter))

(declare-pipeline convert-people-data-to-graph [Dataset -> (Seq Statement)]
                  {dataset "The data file to convert into a graph."})
