package com.banjjoknim.subgraphservice.domain.book.infrastructure

interface MessageChannel {

    fun sendMessage(pubsubMessage: PubSubMessage)
}
