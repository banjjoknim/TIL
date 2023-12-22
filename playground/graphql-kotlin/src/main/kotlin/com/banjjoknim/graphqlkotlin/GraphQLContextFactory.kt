package com.banjjoknim.graphqlkotlin

import com.expediagroup.graphql.server.spring.execution.DefaultSpringGraphQLContextFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest

/**
 * # [Generating GraphQL Context](https://opensource.expediagroup.com/graphql-kotlin/docs/server/spring-server/spring-graphql-context)
 *
 * graphql-kotlin-spring-server provides a Spring specific implementation of GraphQLContextFactory and the context.
 *
 * SpringGraphQLContext (deprecated) - Implements the Spring ServerRequest and federation tracing HTTPRequestHeaders
 *
 * SpringGraphQLContextFactory - Generates GraphQL context map with federated tracing information per request
 *
 * If you are using graphql-kotlin-spring-server, you should extend DefaultSpringGraphQLContextFactory to automatically support federated tracing.
 *
 * Once your application is configured to build your custom GraphQL context map, you can then access it through a data fetching environment argument.
 *
 * While executing the query, data fetching environment will be automatically injected to the function input arguments.
 *
 * This argument will not appear in the GraphQL schema.
 */
@Component
class GraphQLContextFactory : DefaultSpringGraphQLContextFactory() {
    override suspend fun generateContextMap(request: ServerRequest): Map<*, Any> {
        return super.generateContextMap(request) + mapOf(
            "myCustomValue" to (request.headers().firstHeader("MyHeader") ?: "defaultContext")
        )
    }
}
