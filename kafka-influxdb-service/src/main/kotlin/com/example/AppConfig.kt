package com.example

import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import java.util.concurrent.TimeUnit

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
                CLIENT_ID_CONFIG to clientId,
                GROUP_ID_CONFIG to groupId,
                // KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                // VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer<Log>(Log::class.java),
                KEY_DESERIALIZER_CLASS_CONFIG to keyDeserializer,
                VALUE_DESERIALIZER_CLASS_CONFIG to valueDeserializer
        )
        val receiverOptions = ReceiverOptions.create<String, String>(consumerProps)
                .subscription(setOf(topic))

        return KafkaReceiver.create(receiverOptions)
    }
}


@Configuration
class InfluxdbConfig(
        @Value(value = "\${influxdb.url}")
        private val url: String,

        @Value(value = "\${influxdb.username}")
        private val user: String,

        @Value(value = "\${influxdb.password}")
        private val password: String,

        @Value(value = "\${influxdb.database}")
        private val database: String,

        @Value(value = "\${influxdb.retention-policy: \"autogen\"}")
        private val retentionPolicy: String,

        @Value(value = "\${influxdb.consistency-level:#{ T(org.influxdb.InfluxDB.ConsistencyLevel).ALL }}")
        private val consistencyLevel: InfluxDB.ConsistencyLevel,

        @Value(value = "\${influxdb.batch-size: 2000}")
        private val batchSize: Int,

        @Value(value = "\${influxdb.flush-duration-ms: 5000}")
        private val flushDuration: Int

) {
    @Bean
    fun initInfluxDB(): InfluxDB {

        val influxDB = InfluxDBFactory.connect(url, user, password)

        if(!influxDB.databaseExists(database)) {
            influxDB.createDatabase(database)
            influxDB.createRetentionPolicy(retentionPolicy , database, "3d", "30m", 1, true)
        }

        with(influxDB) {
            setDatabase(database)
            setConsistency(consistencyLevel)
            setRetentionPolicy(retentionPolicy)
            enableGzip()
        }

        // influxDB.setLogLevel(InfluxDB.LogLevel.FULL)
        // Flush every 2000 Points, at least every 5000ms
        return influxDB.enableBatch(batchSize, flushDuration, TimeUnit.MILLISECONDS)
    }
}

