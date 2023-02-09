package com.github.banjjoknim.springserversentevent.chatting.application

import com.github.banjjoknim.springserversentevent.chatting.api.ConnectRequest
import com.github.banjjoknim.springserversentevent.chatting.sse.SseData
import com.github.banjjoknim.springserversentevent.chatting.sse.SseEmitters
import org.springframework.stereotype.Service

@Service
class ConnectService(
    private val sseEmitters: SseEmitters
) {

    fun connect(request: ConnectRequest) {
        sseEmitters.emitData(SseData("connect", request))
    }
}
