package com.banjjoknim.subgraphservice.domain.book.api

import com.banjjoknim.subgraphservice.domain.book.datasource.BookDataSource
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component

@Component
class BookGraphQLQuery(
    private val redisTemplate: ReactiveStringRedisTemplate,
) : Query {

    @GraphQLDescription("번호로 책을 집는다")
    fun pickupBook(number: Int): BookResponse {
        val book = BookDataSource.getBook(number)
        redisTemplate.convertAndSend(
            "pickupBook",
            "book has picked up with number: $number. it's title is [${book.title}]"
        ).subscribe()
        return BookResponse(book)
    }
}
