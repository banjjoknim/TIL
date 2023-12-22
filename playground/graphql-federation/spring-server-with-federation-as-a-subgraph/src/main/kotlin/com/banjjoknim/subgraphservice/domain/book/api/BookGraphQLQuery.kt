package com.banjjoknim.subgraphservice.domain.book.api

import com.banjjoknim.subgraphservice.domain.book.application.BookService
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class BookGraphQLQuery(
    private val bookService: BookService,
) : Query {

    @GraphQLDescription("번호로 책을 집는다")
    fun pickupBook(number: Int): BookResponse {
        return bookService.getBook(number)
    }
}
