package com.github.banjjoknim.springserversentevent.chatting.sse

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
class SseController(
    private val sseEmitters: SseEmitters
) {

    @GetMapping("/register-sse")
    fun registerSse(): SseEmitter {
        return sseEmitters.registerSse()
    }
}
