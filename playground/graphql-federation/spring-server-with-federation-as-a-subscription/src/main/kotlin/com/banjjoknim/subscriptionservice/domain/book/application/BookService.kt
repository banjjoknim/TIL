package com.banjjoknim.subscriptionservice.domain.book.application

import com.banjjoknim.subscriptionservice.domain.book.infrastructure.messaging.PubSubMessageChannel
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class BookService(
    private val messageChannel: PubSubMessageChannel,
) {

    fun provideMessageChannel(): Flux<String> {
        return messageChannel.getConnection()
    }
}
