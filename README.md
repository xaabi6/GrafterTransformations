# Grafter Test Transformations

## Datasets
- air-quality
- celica
- people

## Usage

As for now, we've got three different transformations, so the pipeline name will always be one of the names in the above list. Files containing data will also start it's name with the name of the above datasets. The same will apply to the transformations functions itself.

Below, you will find clear examples of usage, having to replace 'PROJECTNAME' with one of the datasets names ;)

### Leiningen

`lein grafter run graftertransformations.pipeline/convert-PROJECTNAME-to-data ./data/PROJECTNAME-data.csv ./output/PROJECTNAME-data.csv`

`lein grafter run graftertransformations.pipeline/convert-PROJECTNAME-data-to-graph ./data/PROJECTNAME-data.csv ./output/PROJECTNAME-data.rdf`

## Travis Tests Results
[![Build Status](https://travis-ci.org/xaabi6/GrafterTransformations.svg?branch=feature-create-tests)](https://travis-ci.org/xaabi6/GrafterTransformations)
