package com.banjjoknim.graphqlkotlin.person

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.Schema
import org.springframework.stereotype.Component

/**
 * In order to expose your schema directives, queries, mutations, and subscriptions in the GraphQL schema create beans that implement the corresponding marker interface and they will be automatically picked up by graphql-kotlin-spring-server auto-configuration library.
 */
@GraphQLDescription("Sample GraphQL Schema")
@Component
class GraphQLSchema : Schema
