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
        /**
         * numPartitions의 값은 Partition의 ID와 무관한 값임. 해당 Topic에 대해 Partition의 갯수를 몇개로 할지 설정하는 것. 그에 따라 0으로 설정해도 무조건 1개 이상의 Partition이 생성됨.
         */
        val topicName = "pickupBook"
        val numPartitions = 1
        val replicationFactor = 1.toShort()
        return NewTopic(topicName, numPartitions, replicationFactor)
    }
}
