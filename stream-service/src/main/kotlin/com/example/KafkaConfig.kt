package com.example

import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions

@Configuration
class KafkaReceiverConfiguration(
        @Value("\${spring.kafka.consumer.bootstrapServers}")
        private val bootstrapServers: String,

        @Value("\${spring.kafka.consumer.group-id}")
        private val groupId: String,

        @Value("\${spring.kafka.consumer.client-id:#{ T(java.net.InetAddress).getLocalHost().getHostName() + \".client\"}}")
        private val clientId: String,

        @Value("\${spring.kafka.consumer.key-deserializer}")
        private val keyDeserializer: String,

        @Value("\${spring.kafka.consumer.value-deserializer}")
        private val valueDeserializer: String,

        @Value("\${spring.kafka.template.default-topic}")
        private val topic: String
) {


    @Bean
    fun kafkaDataReceiver(): KafkaReceiver<String, String> {
        val consumerProps = mapOf(
                BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                CLIENT_ID_CONFIG to  clientId,
                GROUP_ID_CONFIG to groupId,
//                KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
//                VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer<Log>(Log::class.java),
                KEY_DESERIALIZER_CLASS_CONFIG to keyDeserializer,
                VALUE_DESERIALIZER_CLASS_CONFIG to valueDeserializer
        )
        val receiverOptions = ReceiverOptions.create<String, String>(consumerProps)
                .subscription(setOf(topic))

        return KafkaReceiver.create(receiverOptions)
    }


}

