package com.banjjoknim.subscriptionservice.domain.book.api

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Subscription
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class BookGraphQLSubscription(
    private val redisMessageListenerContainer: ReactiveRedisMessageListenerContainer,
) : Subscription {


    companion object {
        private val channelTopic = ChannelTopic.of("pickupBook")
    }

    @GraphQLDescription("사용자가 책을 집을 경우 이에 대한 정보를 알린다")
    fun notifyPickupBook(): Flux<String> {
        return redisMessageListenerContainer.receive(channelTopic)
            .map { it.message }
            .doOnEach { message -> println("Message has received. message: $message") }
    }
}
