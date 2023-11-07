package com.banjjoknim.subgraphservice.domain.book.infrastructure

data class PubSubMessage(
    val destination: String,
    val content: String,
)
