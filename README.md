# Grafter Test Transformations [![Build Status](https://travis-ci.org/xaabi6/GrafterTransformations.svg?branch=master)](https://travis-ci.org/xaabi6/GrafterTransformations) [![Coverage Status](https://coveralls.io/repos/github/xaabi6/GrafterTransformations/badge.svg?branch=master)](https://coveralls.io/github/xaabi6/GrafterTransformations?branch=master)

## Datasets

| FILE | URL | DATE |
| --- | --- | --- |
| air-quality | http://opendata.euskadi.eus/katalogoa/-/airearen-kalitatea-euskadin-2017-urtean/ | 10/04/2017 |
| celica | Created by @xaabi6 | 04/04/2017 |
| people | Created by @xaabi6 | 04/04/2017 |
| work-calendar-2017 | http://opendata.euskadi.eus/katalogoa/-/2017ko-euskadiko-lan-egutegia/ | 18/04/2017 |

## Usage

Pipeline name will always be one of the names in the above list. Files containing data will also start it's name with the name of the above datasets. The same will apply to the transformations functions itself.

Below, you will find clear examples of usage, having to replace 'PROJECTNAME' with one of the datasets names ;)

### Leiningen

`lein grafter run graftertransformations.pipeline/convert-PROJECTNAME-to-data ./data/PROJECTNAME-data.csv ./output/data/PROJECTNAME-data.csv`

`lein grafter run graftertransformations.pipeline/convert-PROJECTNAME-data-to-graph ./data/PROJECTNAME-data.csv ./output/data/PROJECTNAME-data.rdf`

### SPARQL Queries

This code will run a query over the .rdf file you specify. You can also select a file from where queries will be loaded. These files are located under 'queries' folder.

You just need to copy the name of the .rdf file and replace 'FILENAME' with one of the above datasets names, and for specifying the query file, you will need to replace 'QUERYFILE' with one of the file names you can find under 'queries' folder (without the extension, just the name).

`lein repl`

`(use 'graftertransformations.querytest)`

`(def triples (create-triple-store))`

`(.isEmpty (.getConnection triples))`

`(insert-triples triples (java.io.File. "output/data/FILENAME-data.rdf"))`

`(query-result-set triples (with-common-ns-prefixes (from-file "QUERYFILE")))`

`(clear-store triples)`

You will get an output like this one:
![Query Test Output](query-test-output.png?raw=true)
