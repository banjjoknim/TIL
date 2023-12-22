package com.banjjoknim.subgraphservice.domain.book.infrastructure.messaging

interface PubSubMessageChannel {

    fun sendMessage(pubsubMessage: PubSubMessage)
}
