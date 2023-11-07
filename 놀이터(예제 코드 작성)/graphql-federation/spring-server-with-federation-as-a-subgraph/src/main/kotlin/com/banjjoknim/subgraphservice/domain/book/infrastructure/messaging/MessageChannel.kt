package com.banjjoknim.subgraphservice.domain.book.infrastructure.messaging

interface MessageChannel {

    fun sendMessage(pubsubMessage: PubSubMessage)
}
