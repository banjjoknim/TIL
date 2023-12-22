package com.github.banjjoknim.springserversentevent.chatting.sse

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.CopyOnWriteArrayList

@Component
class SseEmitters(
    private val sseEmitters: MutableList<SseEmitter> = CopyOnWriteArrayList(),
    private val objectMapper: ObjectMapper
) {
    companion object {
        private val logger = KLogging().logger
        private val connectDefaultEvent = SseEmitter.event()
            .name("connect")
            .data("connected!")
    }

    fun registerSse(): SseEmitter {
        val sseEmitter = SseEmitter(Long.MAX_VALUE)
        sseEmitters.add(sseEmitter)
        sseEmitter.send(connectDefaultEvent)
        sseEmitter.onCompletion {
            sseEmitters.remove(sseEmitter)
            logger.info { "this is completed sse emitter. so delete this emitter." }
        }
        sseEmitter.onTimeout {
            sseEmitters.remove(sseEmitter)
            sseEmitter.complete()
            logger.info { "timeout occured in this sse emitter. so delete this emitter." }
        }

        return sseEmitter
    }

    fun emitData(sseData: SseData) {
        for (sseEmitter in sseEmitters) {
            val event = SseEmitter.event()
                .name(sseData.eventName)
                .data(objectMapper.writeValueAsString(sseData))
            sseEmitter.send(event)
        }
    }
}
