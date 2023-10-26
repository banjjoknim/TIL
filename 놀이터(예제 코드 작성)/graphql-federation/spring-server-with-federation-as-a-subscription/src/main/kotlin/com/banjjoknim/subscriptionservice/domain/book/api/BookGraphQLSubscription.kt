package com.banjjoknim.subscriptionservice.domain.book.api

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Subscription
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.time.Duration
import kotlin.random.Random

@Component
class BookGraphQLSubscription : Subscription {

    private val books = mapOf(0 to "어린왕자", 1 to "보물섬", 2 to "피터팬")

    @GraphQLDescription("무작위 번호로 책 이름을 조회한다.")
    fun randomBookName(): Flux<String> {
        return Flux.interval(Duration.ofSeconds(1))
            .mapNotNull {
                val number = Random.nextInt(from = 0, until = 3)
                println("number is created. value: $number")
                books[number]
            }
    }
}
