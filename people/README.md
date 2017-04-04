# People Data Transformation

## Usage

Build with this Leiningen commands:

`lein grafter run people.pipeline/convert-people-to-data ./data/people-data.csv ./output/people-data.csv`

`lein grafter run people.pipeline/convert-people-data-to-graph ./data/people-data.csv ./output/people-data.rdf`
