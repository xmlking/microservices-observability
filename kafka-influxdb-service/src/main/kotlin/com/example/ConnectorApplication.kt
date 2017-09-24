package com.example

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.influxdb.InfluxDB
import org.influxdb.dto.Point
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import reactor.kafka.receiver.KafkaReceiver
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


@SpringBootApplication
class ConnectorApplication


@Service
class InfluxService(private val influxDB: InfluxDB) {

    var zoneId = ZoneId.systemDefault()

    fun showDatabases(): List<String> {
        return influxDB.describeDatabases()
    }

    fun writePoints(points: List<Point>) {
        points.forEach { point ->
            influxDB.write(point)
        }
    }

    fun writePoint(measurement: String, tagMap: Map<String, String>, fieldMap: Map<String, Any>) {

        val point = Point.measurement(measurement)
                .tag(tagMap)
                .fields(fieldMap)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build();

        influxDB.write(point)
    }

    fun writeLog(log: Log) {

        val logPoint = Point.measurement("logs")
                .time(log.timestamp.atZone(zoneId).toInstant().toEpochMilli(), TimeUnit.MILLISECONDS)
                .addField("version", log.version)
                .addField("level_value", log.levelValue)
                .addField("logger_name", log.loggerName)
                .addField("thread_name", log.threadName)
                .addField("message", log.message)

                .tag("HOSTNAME", log.hostname)
                .tag("app", log.appName)
                .tag("level", log.level.name)

                .build()

        influxDB.write(logPoint)
    }

    fun writeLog(log: JsonNode) {
        val logPoint = Point.measurement("logs")
        val timestamp =  LocalDateTime.parse(log.get("timestamp").asText(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            .atZone(zoneId).toInstant().toEpochMilli()

        logPoint.time(timestamp, TimeUnit.MILLISECONDS)

        log.fields().forEach {
            when (it.key) {
                "HOSTNAME", "app", "level" -> logPoint.tag(it.key, log.get(it.key).asText())

                "version" -> logPoint.addField(it.key, log.get(it.key).asInt())
                "level_value" -> logPoint.addField(it.key, log.get(it.key).asInt())
                else ->  logPoint.addField(it.key, log.get(it.key).asText())
            }
        }

        influxDB.write(logPoint.build())
    }


    fun close() {
        influxDB.close();
    }
}


@Component
internal class AppLifecycleHooks(private val influxService: InfluxService,
                                 private val kafkaDataReceiver: KafkaReceiver<String, String>,
                                 private val objectMapper: ObjectMapper) {

    @EventListener(ContextRefreshedEvent::class)
    fun init() {

        // val logStream = kafkaDataReceiver.receive().map { objectMapper.readValue(it.value(), Log::class.java) }.share()
        val logStream = kafkaDataReceiver.receive().map { objectMapper.readTree(it.value()) }.share()

        logStream.subscribe {
            influxService.writeLog(it)
        }

    }
}


fun main(args: Array<String>) {
    SpringApplication.run(ConnectorApplication::class.java, *args)
}

