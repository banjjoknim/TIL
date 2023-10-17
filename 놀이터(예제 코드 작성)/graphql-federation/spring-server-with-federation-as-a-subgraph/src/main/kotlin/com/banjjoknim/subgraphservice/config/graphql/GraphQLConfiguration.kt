package com.banjjoknim.subgraphservice.config.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import com.expediagroup.graphql.server.Schema
import graphql.GraphQL
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.AsyncSerialExecutionStrategy
import graphql.execution.DataFetcherExceptionHandler
import graphql.schema.GraphQLSchema
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class GraphQLConfiguration {

    @GraphQLDescription("Sample GraphQL Schema")
    @Bean
    fun graphQLSchema(): Schema {
        return object : Schema {}
    }

    @Bean
    fun federatedSchemaGeneratorHooks(resolvers: Optional<List<FederatedTypeResolver>>): FederatedSchemaGeneratorHooks {
        return CustomFederationSchemaGeneratorHooks(resolvers.orElse(emptyList()))
    }


    @Bean
    fun graphQL(schema: GraphQLSchema?): GraphQL {
        val dataFetcherExceptionHandler = object : DataFetcherExceptionHandler {} // 기본 구현체 사용
        return GraphQL.newGraphQL(schema)
            .queryExecutionStrategy(AsyncExecutionStrategy(dataFetcherExceptionHandler))
            .mutationExecutionStrategy(AsyncSerialExecutionStrategy(dataFetcherExceptionHandler))
            .build()
    }
}
