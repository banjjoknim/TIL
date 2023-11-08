package com.banjjoknim.subscriptionservice.domain.book.api

import com.banjjoknim.subscriptionservice.domain.book.application.BookService
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Subscription
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class BookGraphQLSubscription(
    private val bookService: BookService,
) : Subscription {

    @GraphQLDescription("사용자가 책을 집을 경우 발행되는 알림을 구독한다")
    fun subscribePickupBookNotiChannel(): Flux<String> {
        return bookService.provideMessageChannel()
    }
}
