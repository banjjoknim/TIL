package com.banjjoknim.subgraphservice.domain.book.infrastructure.messaging

data class PubSubMessage(
    val destination: String,
    val content: String,
)
