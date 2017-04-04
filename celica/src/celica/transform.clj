(ns celica.transform
  (:require
    [clojure.string :as st]
    [grafter.rdf.protocols :as pr]
    [grafter.rdf.io :as io]
    [celica.prefix :refer :all]
  )
)

;;; You can specify transformation functions in this namespace for use
;;; within the pipeline.

(defn integer
  "An example transformation function that converts a string to an integer"
  [s]
  (Integer/parseInt s)
)

(defn add-cubic-centimetre [st]
  (str st "cm³")
)

(defn add-revolutions-per-minute [st]
  (str st "rpm")
)

(defn add-millimeters [st]
  (str st "mm")
)

(defn add-kilograms [st]
  (str st "kg")
)

(defn add-litres [st]
  (str st "l")
)

(defn add-euro-symbol [st]
  (str st "€")
)

;; Still not working :(

;(defn add-euro-symbol [st]
;  (if-not (empty? st) (str st "€"))
;)

;(defn add-euro-symbol [st]
;  (if-not (st/includes? "N/D" st) (str st "€"))
;)

;(defn add-euro-symbol [st]
;  (when-not (.contains st "N/D") (str st "€"))
;)

;(defmethod add-euro-symbol java.lang.String [st]
;  (when-not (.contains st "N/D")
;      (str st "€")
;  )
;)

(defn missing-data-filter [triples]
  (remove #(nil? (pr/object %)) triples)
)

(defn urlify [sr]
  (st/replace (st/trim sr) #"\(|\)|\s|\/|\." "-")
)

(defn celica-uri [a b c d]
  (base-id
    (str "Car/Specifications/" (urlify
        (str a "-" b "-" c "-" d)
      )
    )
  )
)

(defn data-uri [a b]
  (base-id
    (str "Car/" (urlify
        (str a "-" b)
      )
    )
  )
)

(defn commentary [st]
    (io/s st)
)

(def car-manufacturer (base-domain "/property/brand"))

(def car-name (base-domain "/property/name"))

(def start-manufacture (base-domain "/property/startedProduction"))

(def end-manufacture (base-domain "/property/finishedProduction"))

(def platform-types (base-domain "/property/platform"))

(def engine-code (base-domain "/property/engineCode"))

(def car-version (base-domain "/property/version"))

(def engine-size (base-domain "/property/engineSize"))

(def engine-valves (base-domain "/property/engineValves"))

(def engine-maxTorque (base-domain "/property/engineMaxTorque"))

(def car-traction (base-domain "/property/traction"))

(def measurements-length (base-domain "/property/length"))

(def measurements-width (base-domain "/property/width"))

(def measurements-height (base-domain "/property/height"))

(def measurements-curbWeight (base-domain "/property/curbWeight"))

(def car-fuel (base-domain "/property/fuel"))

(def car-fuel-tank (base-domain "/property/fuelTank"))

(def car-airbags (base-domain "/property/airbags"))

(def car-doors (base-domain "/property/doors"))

(def car-price (base-domain "/property/priceSpain"))
