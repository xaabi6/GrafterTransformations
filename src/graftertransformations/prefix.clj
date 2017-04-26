(ns graftertransformations.prefix
  (:require [grafter.rdf :refer [prefixer]]))

;; Defines what will be useful for our next data transformations

(def base-domain (prefixer "http://opendata.eurohelp.es"))

(def base-graph (prefixer (base-domain "/graph/")))

(def base-id (prefixer (base-domain "/resource/")))

(def base-air-quality (prefixer (base-domain "/station/")))

(def base-calendar (prefixer (base-domain "/jaiegunak/")))

(def base-vocab (prefixer (base-domain "/def/")))

(def base-data (prefixer (base-domain "/data/")))
