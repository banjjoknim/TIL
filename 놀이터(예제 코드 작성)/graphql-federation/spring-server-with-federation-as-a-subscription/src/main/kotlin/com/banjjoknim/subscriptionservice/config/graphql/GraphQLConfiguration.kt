package com.banjjoknim.subscriptionservice.config.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.execution.FlowSubscriptionExecutionStrategy
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
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

    /**
     *
     * ```graphql
     * query {
     *   _service {
     *     sdl
     *   }
     * }
     * ```
     *
     * - 참고 : Federation 설정이 진행되면 위 쿼리를 이용해서 SuperGraph 에 SubGraph를 제공할 수 있게 됨.
     * - 참고 : 작성시점을 기준으로 제공되는 스키마 정보는 federation 버전이 2.5임. 그에 맞게 게이트웨이에서 수집하는 federation 버전도 수정해주어야 함.
     * @see com.expediagroup.graphql.generator.SchemaGenerator
     * @see com.expediagroup.graphql.generator.SchemaGeneratorConfig
     * @see com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
     * @see com.expediagroup.graphql.generator.hooks.NoopSchemaGeneratorHooks
     * @see com.expediagroup.graphql.generator.hooks.FlowSubscriptionSchemaGeneratorHooks
     * @see com.expediagroup.graphql.server.spring.NonFederatedSchemaAutoConfiguration
     *
     * @see com.expediagroup.graphql.generator.federation.FederatedSchemaGenerator
     * @see com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
     * @see com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
     * @see com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks.willBuildSchema // 이 함수에서 Federation 관련 설정이 진행됨.
     * @see com.expediagroup.graphql.generator.federation.types.SERVICE_OBJECT_TYPE // 상단에 명시된 쿼리 참조.
     */
    @Bean
    fun federatedSchemaGeneratorHooks(resolvers: Optional<List<FederatedTypeResolver>>): SchemaGeneratorHooks {
        return CustomFederationSchemaGeneratorHooks(resolvers.orElse(emptyList()))
    }

    /**
     * @see com.expediagroup.graphql.server.spring.GraphQLSchemaConfiguration
     * @see com.expediagroup.graphql.server.spring.SubscriptionAutoConfiguration
     * @see com.expediagroup.graphql.generator.execution.FlowSubscriptionExecutionStrategy
     * @see com.expediagroup.graphql.server.execution.subscription.GraphQLWebSocketServer
     * @see com.expediagroup.graphql.server.spring.SubscriptionGraphQLWsAutoConfiguration
     */
    @Bean
    fun graphQL(schema: GraphQLSchema?): GraphQL {
        val dataFetcherExceptionHandler = object : DataFetcherExceptionHandler {} // 기본 구현체 사용
//        val customDataFetcherExceptionHandler = CustomDataFetcherExceptionHandler() // 사용자 정의 구현체 사용
        return GraphQL.newGraphQL(schema)
            .queryExecutionStrategy(AsyncExecutionStrategy(dataFetcherExceptionHandler))
            .mutationExecutionStrategy(AsyncSerialExecutionStrategy(dataFetcherExceptionHandler))
            .subscriptionExecutionStrategy(FlowSubscriptionExecutionStrategy(dataFetcherExceptionHandler))
            .build()
    }
}
