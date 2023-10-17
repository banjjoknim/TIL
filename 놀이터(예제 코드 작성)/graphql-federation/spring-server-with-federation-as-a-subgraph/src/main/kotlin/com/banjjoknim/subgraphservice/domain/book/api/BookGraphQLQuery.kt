package com.banjjoknim.subgraphservice.domain.book.api

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class BookGraphQLQuery : Query {

    @GraphQLDescription("제목으로 책을 조회한다.")
    fun getBook(title: String): Book {
        return Book(title)
    }
}
