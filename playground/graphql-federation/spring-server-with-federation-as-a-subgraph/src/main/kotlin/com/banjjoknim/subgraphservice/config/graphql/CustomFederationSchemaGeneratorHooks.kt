package com.banjjoknim.subgraphservice.config.graphql

import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import graphql.language.IntValue
import graphql.language.StringValue
import graphql.schema.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KType

class CustomFederationSchemaGeneratorHooks(resolvers: List<FederatedTypeResolver>) :
    FederatedSchemaGeneratorHooks(resolvers) {

    private val javaLongType = GraphQLScalarType.newScalar()
        .name("JavaLongType")
        .description("JavaLongType")
        .coercing(object : Coercing<Long, Int> {
            override fun serialize(dataFetcherResult: Any): Int = dataFetcherResult.toString().toInt()
            override fun parseValue(input: Any): Long = input.toString().toLong()
            override fun parseLiteral(input: Any): Long = (input as? IntValue)?.value?.toLong()
                ?: throw IllegalArgumentException("")
        })
        .build()


    private val localDateTimeType = GraphQLScalarType.newScalar()
        .name("LocalDateTime")
        .description("java LocalDateTime")
        .coercing(LocalDateTimeCoercing)
        .build()

    private object LocalDateTimeCoercing : Coercing<LocalDateTime, String> {
        override fun serialize(dataFetcherResult: Any): String {
            return when (dataFetcherResult) {
                is LocalDateTime -> dataFetcherResult.plusHours(9)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

                else -> dataFetcherResult.toString()
            }
        }

        override fun parseValue(input: Any): LocalDateTime {
            return LocalDateTime.parse(input.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .minusHours(9)
        }

        override fun parseLiteral(input: Any): LocalDateTime {
            val stringValue = input as? StringValue
            return LocalDateTime.parse(stringValue?.value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .minusHours(9)
        }
    }

    private val graphqlUUIDType = GraphQLScalarType.newScalar()
        .name("UUID")
        .description("A type representing a formatted java.util.UUID")
        .coercing(UUIDCoercing)
        .build()

    private object UUIDCoercing : Coercing<UUID, String> {
        override fun parseValue(input: Any): UUID = try {
            UUID.fromString(
                serialize(input)
            )
        } catch (e: Exception) {
            throw CoercingParseValueException("Cannot parse value $input to UUID", e)
        }

        override fun parseLiteral(input: Any): UUID = try {
            val uuidString = (input as? StringValue)?.value
            UUID.fromString(uuidString)
        } catch (e: Exception) {
            throw CoercingParseLiteralException("Cannot parse literal $input to UUID", e)
        }

        override fun serialize(dataFetcherResult: Any): String = dataFetcherResult.toString()
    }

    /**
     * Register additional GraphQL scalar types.
     */
    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
        LocalDateTime::class -> localDateTimeType
        Long::class -> javaLongType
        UUID::class -> graphqlUUIDType
        else -> super.willGenerateGraphQLType(type)
    }
}


