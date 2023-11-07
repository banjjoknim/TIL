package com.banjjoknim.subgraphservice.domain.book.infrastructure.messaging.kafka

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("kafka")
@Configuration
class KafkaTopicConfiguration {

    /**
     * Bean으로 NewTopic을 등록하면 서버 기동시 Topic을 자동으로 생성한다.
     *
     * @see org.apache.kafka.clients.admin.NewTopic
     */
    @Bean
    fun pickupBookTopic(): NewTopic {
        return NewTopic("pickupBook", 0, 1.toShort())
    }
}
