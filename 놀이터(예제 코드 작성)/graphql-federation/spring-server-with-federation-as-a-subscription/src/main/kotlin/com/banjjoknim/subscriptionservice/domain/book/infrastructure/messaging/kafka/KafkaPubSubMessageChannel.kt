package com.banjjoknim.subscriptionservice.domain.book.infrastructure.messaging.kafka

import com.banjjoknim.subscriptionservice.domain.book.infrastructure.messaging.PubSubMessageChannel
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

@Profile("kafka")
@Component
class KafkaPubSubMessageChannel : PubSubMessageChannel {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 참고 자료
     * - [Why is Sinks.many().multicast().onBackpressureBuffer() completing after one of the subscribers cancels the subscription and how to avoid it](https://stackoverflow.com/questions/66671636/why-is-sinks-many-multicast-onbackpressurebuffer-completing-after-one-of-t)
     * - [Overview of Available Sinks](https://projectreactor.io/docs/core/release/reference/#sinks-overview)
     */
    private val pickupBookSink: Sinks.Many<String> = Sinks.many().multicast().directAllOrNothing()

    @KafkaListener(groupId = "pickupBook", topics = ["pickupBook"])
    fun listen(record: ConsumerRecord<String, String>) {
        pickupBookSink.tryEmitNext(record.value())
        println("listen complete. record.value: ${record.value()}")
    }

    override fun getConnection(): Flux<String> {
        return pickupBookSink.asFlux()
            .doOnEach { message -> logger.info("message has received. message: $message") }
    }
}
