package com.banjjoknim.subgraphservice.domain.book.api

import com.banjjoknim.subgraphservice.domain.book.datasource.BookDataSource
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class BookGraphQLQuery : Query {

    @GraphQLDescription("번호로 책을 조회한다.")
    fun getBook(number: Int): BookResponse {
        val book = BookDataSource.getBook(number)
        return BookResponse(book)
    }
}
