package com.banjjoknim.subscriptionservice.config.graphql

import graphql.GraphQLError
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import graphql.language.SourceLocation

class CustomDataFetcherExceptionHandler : DataFetcherExceptionHandler {

    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        handlerParameters.exception.printStackTrace()
        val errors = when (val exception = handlerParameters.exception) {
            else -> {
                exception.printStackTrace()
                listOf(customGraphQLError(message = exception.localizedMessage ?: "your error message"))
            }
        }

        return DataFetcherExceptionHandlerResult.newResult()
            .errors(errors)
            .build()
    }

    private fun customGraphQLError(message: String): GraphQLError {
        return object : GraphQLError {
            override fun getMessage() = message

            override fun getLocations() = mutableListOf<SourceLocation>()

            override fun getErrorType() = null

            override fun getExtensions() = mapOf<String, Any>()
        }
    }
}
