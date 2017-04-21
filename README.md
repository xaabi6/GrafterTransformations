# Grafter Test Transformations [![Build Status](https://travis-ci.org/xaabi6/GrafterTransformations.svg?branch=master)](https://travis-ci.org/xaabi6/GrafterTransformations) [![Coverage Status](https://coveralls.io/repos/github/xaabi6/GrafterTransformations/badge.svg?branch=master)](https://coveralls.io/github/xaabi6/GrafterTransformations?branch=master)

## Datasets
- air-quality
- celica
- people
- work-calendar-2017

## Usage

Pipeline name will always be one of the names in the above list. Files containing data will also start it's name with the name of the above datasets. The same will apply to the transformations functions itself.

Below, you will find clear examples of usage, having to replace 'PROJECTNAME' with one of the datasets names ;)

### Leiningen

`lein grafter run graftertransformations.pipeline/convert-PROJECTNAME-to-data ./data/PROJECTNAME-data.csv ./output/data/PROJECTNAME-data.csv`

`lein grafter run graftertransformations.pipeline/convert-PROJECTNAME-data-to-graph ./data/PROJECTNAME-data.csv ./output/data/PROJECTNAME-data.rdf`
