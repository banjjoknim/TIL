package com.github.banjjoknim.springserversentevent.chatting.sse

import mu.KLogging
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.time.LocalDateTime

/**
 * [Spring WebFlux (Flux): how to publish dynamically](https://stackoverflow.com/questions/51370463/spring-webflux-flux-how-to-publish-dynamically) 참조.
 */
@Component
object SseSinks {
    private val logger = KLogging().logger
    private val sinks = Sinks.many().multicast().directBestEffort<SseData>()

    fun registerSse(): Flux<ServerSentEvent<SseData>> {
        return sinks.asFlux()
            .doOnError { error -> logger.error { "$error occured in SseSinks#registerSse()" } }
            .flatMap { it.toServerSentEvent() }
            .defaultIfEmpty(ServerSentEvent.builder(SseData.DEFAULT).build())
    }

    private fun SseData.toServerSentEvent(): Flux<ServerSentEvent<SseData>> {
        val serverSentEvent = ServerSentEvent.builder<SseData>()
            .id("${LocalDateTime.now()}")
            .event(eventName)
            .data(this)
            .build()
        return Flux.just(serverSentEvent)
    }

    fun emitData(sseData: SseData) {
        sinks.tryEmitNext(sseData)
    }
}
