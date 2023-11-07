package com.banjjoknim.subgraphservice.domain.book.infrastructure.messaging.kafka

import com.banjjoknim.subgraphservice.domain.book.infrastructure.messaging.PubSubMessage
import com.banjjoknim.subgraphservice.domain.book.infrastructure.messaging.PubSubMessageChannel
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Profile("kafka")
@Component
class KafkaPubSubMessageChannel(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : PubSubMessageChannel {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun sendMessage(pubsubMessage: PubSubMessage) {
        val topic = pubsubMessage.destination
        val data = pubsubMessage.content
        val future: CompletableFuture<SendResult<String?, String?>> = kafkaTemplate.send(topic, data)
        future.whenComplete { result: SendResult<String?, String?>, ex: Throwable? ->
            if (ex == null) {
                logger.info("Sent message=[$data] with offset=[${result.recordMetadata.offset()}]")
            } else {
                logger.info("Unable to send message=[$data] due to : ${ex.message}")
            }
        }
    }

}
