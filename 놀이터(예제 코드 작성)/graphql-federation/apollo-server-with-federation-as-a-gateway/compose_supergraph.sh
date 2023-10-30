# Creates prod-schema.graphql or overwrites if it already exists
rover supergraph compose --config ./supergraph.yaml --elv2-license accept --output supergraph.graphql
#rover supergraph compose --config ./supergraph.yaml --output prod-schema.graphql
# `--elv2-license accept` 옵션에 대해서는 https://www.apollographql.com/docs/rover/commands/supergraphs/#federation-2-elv2-license 참조.
