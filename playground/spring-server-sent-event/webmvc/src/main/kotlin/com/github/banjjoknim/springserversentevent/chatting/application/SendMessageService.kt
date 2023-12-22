package com.github.banjjoknim.springserversentevent.chatting.application

import com.github.banjjoknim.springserversentevent.chatting.api.SendMessageRequest
import com.github.banjjoknim.springserversentevent.chatting.sse.SseData
import com.github.banjjoknim.springserversentevent.chatting.sse.SseEmitters
import org.springframework.stereotype.Service

@Service
class SendMessageService(
    private val sseEmitters: SseEmitters
) {

    fun sendMessage(request: SendMessageRequest) {
        sseEmitters.emitData(SseData("message", request))
    }
}
