package com.github.banjjoknim.springserversentevent.chatting.application

import com.github.banjjoknim.springserversentevent.chatting.api.ConnectRequest
import com.github.banjjoknim.springserversentevent.chatting.sse.SseData
import com.github.banjjoknim.springserversentevent.chatting.sse.SseSinks
import org.springframework.stereotype.Service

@Service
class ConnectService {

    fun connect(request: ConnectRequest) {
        SseSinks.emitData(SseData("connect", request))
    }
}
