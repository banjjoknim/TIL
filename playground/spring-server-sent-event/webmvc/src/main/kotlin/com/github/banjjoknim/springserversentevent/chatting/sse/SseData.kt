package com.github.banjjoknim.springserversentevent.chatting.sse

data class SseData(
    val eventName: String,
    val data: Any? = null
)
