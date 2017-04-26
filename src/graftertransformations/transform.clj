(ns graftertransformations.transform
  (:require
    [clojure.string :as st]
    [grafter.rdf.protocols :as pr]
    [grafter.rdf.io :as io]
    [graftertransformations.prefix :refer :all]
  )
)

;;; You can specify transformation functions in this namespace for use
;;; within the pipeline.

;; GENERAL
(defn integer [s]
  (Integer/parseInt s)
)

(defn missing-data-filter [triples]
  (remove #(nil? (pr/object %)) triples)
)

(defn urlify [sr]
  (st/replace (st/trim sr) #"\(|\)|\s|\/|\." "-")
)

(defn commentary [st]
  (io/s st)
)

;Español
(defn langSp [st]
  (io/s st :es)
)

;English
(defn langEn [st]
  (io/s st :en)
)

;Euskera
(defn langVq [st]
  (io/s st :eu)
)

(defn removeSymbols [st]
  (let [replace clojure.string/replace]
    (-> (str st)
        clojure.string/trim
        (replace "(" "-")
        (replace ")" "")
        (replace "  " "")
        (replace "," "-")
        (replace "." "")
        (replace " " "-")
        (replace "/" "-")
        (replace "'" "")
        (replace "---" "-")
        (replace "--" "-")
     )
   )
)

;Convertidor a Float o Integer
(defmulti parseValue class)
(defmethod parseValue :default            [x] x)
(defmethod parseValue nil                 [x] nil)
(defmethod parseValue java.lang.Character [x] (Character/getNumericValue x))
(defmethod parseValue java.lang.String    [x] (if (= "" x)
                                                nil
                                                (if (.contains x ".")
                                                  (Double/parseDouble x)
                                                  (Integer/parseInt x)
                                                )
                                              ))

;Cambia el formato de la fecha [dd/mm/yyyy ~> yyyy-mm-dd]
(defn organizeDate [date]
  (when (seq date)
    (let [[d m y] (st/split date #"/")]
      (apply str (interpose "-" [y m d] ))
    )
  )
)

;; AIR QUALITY
(defn quality-uri [a]
  (base-air-quality
    (str (removeSymbols a))
  )
)

;; CELICA
(defn celica-uri [a b c d]
  (base-id
    (str "Car/Specifications/" (urlify
        (str a "-" b "-" c "-" d)
      )
    )
  )
)

;;TODO: This 'extended' functions should be done in a single generic one, but I will have to find out how

(defn celica-displacement-extended-uri [a b c d e]
  (base-id
    (str "Car/displacement/" (urlify
        (str a "-" b "-" c "-" d "-" e)
      )
    )
  )
)

(defn celica-torqueOutput-extended-uri [a b c d e]
  (base-id
    (str "Car/torqueOutput/" (urlify
        (str a "-" b "-" c "-" d "-" e)
      )
    )
  )
)

(defn celica-dimensions-extended-uri [a b c d e f g]
  (base-id
    (str "Car/dimensions/" (urlify
        (str a "-" b "-" c "-" d "-" e "-" f "-" g)
      )
    )
  )
)

(defn celica-curbWeight-extended-uri [a b c d e]
  (base-id
    (str "Car/curbWeight/" (urlify
        (str a "-" b "-" c "-" d "-" e)
      )
    )
  )
)

(defn celica-fuelCapacity-extended-uri [a b c d e]
  (base-id
    (str "Car/fuelCapacity/" (urlify
        (str a "-" b "-" c "-" d "-" e)
      )
    )
  )
)

(defn celica-price-extended-uri [a b c d e]
  (base-id
    (str "Car/price/" (urlify
        (str a "-" b "-" c "-" d "-" e)
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

(def platform-types (base-domain "/property/platform"))

(def engine-code (base-domain "/property/engineCode"))

(def engine-displacement (base-domain "/property/engineDisplacement"))

(def engine-torque (base-domain "/property/engineTorque"))

(def measurements-size (base-domain "/property/size"))

(def measurements-curbWeight (base-domain "/property/curbWeight"))

(def car-fuel-tank (base-domain "/property/fuelTank"))

(def car-traction (base-domain "/property/traction"))

(def car-price (base-domain "/property/priceSpain"))

;; PEOPLE
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

(def base-marriage (base-domain "/property/married"))

(def base-surname (base-domain "/property/surname"))

(def base-email (base-domain "/property/email"))

;; WORK CALENDAR 2017
(defn replaceDashes [st]
  (let [replace clojure.string/replace]
    (-> (str st)
        clojure.string/trim
        (replace " - " "/")
     )
   )
)

(defn adaptDescriptions [st]
  (let [replace clojure.string/replace]
    (-> (str st)
        clojure.string/trim
        (replace "  " "")
     )
   )
)

(defn calendar-uri [a]
  (base-calendar
    (urlify
      (str a)
    )
  )
)

(def base-eustatCode (base-domain "/property/EustatCode"))
