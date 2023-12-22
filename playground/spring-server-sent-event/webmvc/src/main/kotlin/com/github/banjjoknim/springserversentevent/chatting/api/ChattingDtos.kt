package com.github.banjjoknim.springserversentevent.chatting.api

data class ConnectRequest(
    val userName: String
)

data class SendMessageRequest(
    val userName: String,
    val content: String
)
