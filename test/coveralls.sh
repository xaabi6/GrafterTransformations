COVERALLS_URL='https://coveralls.io/api/v1/jobs'
CLOVERAGE_VERSION='1.0.9' lein2 cloverage -o xaabi6 --coveralls
curl -F 'json_file=@xaabi6/coveralls.json' "$COVERALLS_URL"
