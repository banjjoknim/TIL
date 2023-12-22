package com.banjjoknim.subscriptionservice.domain.book.infrastructure.messaging

import reactor.core.publisher.Flux

interface PubSubMessageChannel {

    fun getConnection(): Flux<String>
}
