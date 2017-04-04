(ns people.transform
  (:require
    [clojure.string :as st]
    [grafter.rdf.protocols :as pr]
    [grafter.rdf.io :as io ]
    [people.prefix :refer :all]
  )
)

;; Data transformations

(defn integer
  "An example transformation function that converts a string to an integer"
  [s]
  (Integer/parseInt s)
)

(defn missing-data-filter [triples]
  (remove #(nil? (pr/object %)) triples))

(defn urlify [sr]
  (st/replace (st/trim sr) #"\(|\)|\s|\/|\." "-")
)

(defn person-uri [a b]
  (base-id
    (str "Person/" (urlify
        (str a "-" b)
      )
    )
  )
)

(defn info-uri [a]
  (base-id
    (str "Info/" (urlify
        (str a)
      )
    )
  )
)

(defn commentary
  [st]
    (io/s st))

(def base-marriage (base-domain "/property/married"))

(def base-surname (base-domain "/property/surname"))

(def base-email (base-domain "/property/email"))
