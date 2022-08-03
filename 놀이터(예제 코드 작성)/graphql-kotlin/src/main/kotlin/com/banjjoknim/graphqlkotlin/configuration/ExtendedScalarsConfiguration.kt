package com.banjjoknim.graphqlkotlin.configuration

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import graphql.language.StringValue
import graphql.scalars.ExtendedScalars
import graphql.scalars.util.Kit.typeName
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * - [Extended Scalars for graphql-java](https://github.com/graphql-java/graphql-java-extended-scalars)
 *
 * - [Cannot use java.util.Date](https://github.com/ExpediaGroup/graphql-kotlin/discussions/1198)
 *
 * - [GraphQL Kotlin - Extended Scalars](https://opensource.expediagroup.com/graphql-kotlin/docs/schema-generator/writing-schemas/scalars/#common-issues)
 *
 * - [GraphQL Kotlin - Generator Configuration & Hooks](https://opensource.expediagroup.com/graphql-kotlin/docs/schema-generator/customizing-schemas/generator-config)
 */
@Configuration
class ExtendedScalarsConfiguration {
    /**
     * 아래와 같이 Bean으로 Hook을 등록해주면 Schema Generator가 Schema를 생성할 때 이 Bean에 정의된 Hook을 이용해서 Schema를 만든다.
     */
    @Bean
    fun extendedScalarsHooks(): SchemaGeneratorHooks {
        return object : SchemaGeneratorHooks {
            override fun willGenerateGraphQLType(type: KType): GraphQLType? {
                return when (type.classifier as? KClass<*>) {
                    Long::class -> ExtendedScalars.GraphQLLong
                    LocalDateTime::class -> localDateTimeScalar()
                    LocalTime::class -> ExtendedScalars.LocalTime
                    LocalDate::class -> ExtendedScalars.Date
                    else -> null
                }
            }
        }
    }

    /**
     * Bean으로 ScalarType을 등록해주지 않으면 어플리케이션 실행시 스키마를 구성하는 단계(스키마에 포함될 타입중에서 LocalDateTime 이 포함되어 있는 경우)에서 아래와 같은 예외가 발생한다.
     *
     * ```
     * graphql.AssertException: All types within a GraphQL schema must have unique names. No two provided types may have the same name.
     * No provided type may have a name which conflicts with any built in types (including Scalar and Introspection types). You have redefined the type 'LocalDateTime' from being a 'GraphQLScalarType' to a 'GraphQLScalarType'
     * ```
     *
     * @see graphql.scalars.datetime.DateTimeScalar
     */
    @Bean
    fun localDateTimeScalar(): GraphQLScalarType? {
        val coercing = object : Coercing<LocalDateTime, String> {
            override fun serialize(dataFetcherResult: Any): String {
                return when (dataFetcherResult) {
                    is LocalDateTime -> DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(
                        LocalDateTime.from(dataFetcherResult)
                    )
                    is String -> DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(
                        LocalDateTime.parse(dataFetcherResult)
                    )
                    else -> throw CoercingSerializeException(
                        "Expected something we can convert to 'java.time.LocalDateTime' but was '" +
                            "${typeName(dataFetcherResult)}'."
                    )
                }
            }

            override fun parseValue(input: Any): LocalDateTime {
                return when (input) {
                    is LocalDateTime -> input
                    is String -> LocalDateTime.parse(
                        input.toString(),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    )
                    else -> throw CoercingParseValueException(
                        "Expected a 'String' but was '" + "${typeName(input)}'."
                    )
                }
            }

            override fun parseLiteral(input: Any): LocalDateTime {
                if (input !is StringValue) {
                    throw CoercingParseLiteralException(
                        "Expected AST type 'StringValue' but was '${typeName(input)}'."
                    )
                }
                return LocalDateTime.parse(input.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            }
        }
        return GraphQLScalarType.newScalar()
            .name("LocalDateTime")
            .description("Custom LocalDateTime Scalar")
            .coercing(coercing)
            .build()
    }
}
