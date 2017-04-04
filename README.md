# Grafter Test Transformations

## Usage

As for now, we've got two different transformations, so the pipeline name will always be the name of the folder are located in. Files containing data will also start it's name with the name of the folder. The same will apply to the transformations functions itself.

Below, you will find clear examples of usage, having to replace 'PROJECTNAME' with one of the available folder names, at the moment there are only two (celica and people) ;)

### Leiningen

`lein grafter run PROJECTNAME.pipeline/convert-PROJECTNAME-to-data ./data/PROJECTNAME-data.csv ./output/PROJECTNAME-data.csv`

`lein grafter run PROJECTNAME.pipeline/convert-PROJECTNAME-data-to-graph ./data/PROJECTNAME-data.csv ./output/PROJECTNAME-data.rdf`

## TODO

- When a cell in the csv file is empty, do not create a triple, because it's useless
