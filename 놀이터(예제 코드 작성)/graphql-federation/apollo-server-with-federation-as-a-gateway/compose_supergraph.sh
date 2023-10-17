# Creates prod-schema.graphql or overwrites if it already exists
rover supergraph compose --config ./supergraph.yaml --output supergraph.graphql
#rover supergraph compose --config ./supergraph.yaml --output prod-schema.graphql
