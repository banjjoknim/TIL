package com.github.banjjoknim.springserversentevent.chatting.application

import com.github.banjjoknim.springserversentevent.chatting.api.SendMessageRequest
import com.github.banjjoknim.springserversentevent.chatting.sse.SseData
import com.github.banjjoknim.springserversentevent.chatting.sse.SseSinks
import org.springframework.stereotype.Service

@Service
class SendMessageService {

    fun sendMessage(request: SendMessageRequest) {
        SseSinks.emitData(SseData("message", request))
    }
}
