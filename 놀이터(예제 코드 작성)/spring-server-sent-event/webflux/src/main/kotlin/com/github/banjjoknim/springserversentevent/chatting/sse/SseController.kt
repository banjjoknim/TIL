package com.github.banjjoknim.springserversentevent.chatting.sse

import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class SseController {

    @GetMapping("/register-sse")
    fun registerSse(): Flux<ServerSentEvent<SseData>> {
        return SseSinks.registerSse()
    }
}
