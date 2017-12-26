package com.example

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.io.IOException
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// ----------------
//  for Kotlin Gradle NoArg plugin
// ----------------
annotation class NoArg

data class Quote(val ticker: String, val price: BigDecimal, val instant: Instant = Instant.now())

data class GuestBookEntryDTO(
        val name: String,
        val comment: String,
        val time: LocalDateTime = LocalDateTime.now()
) {
    val stringDate: String
        get() = "$time"
}

enum class Level {
    ERROR, WARN, INFO, DEBUG, TRACE
}

@JsonRootName("log")
@JsonIgnoreProperties(ignoreUnknown = true)
data class Log(
        @JsonProperty("timestamp")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        @JsonSerialize(using = MyLocalDateTimeSerializer::class)
        val timestamp: LocalDateTime,
        @JsonProperty("version")
        val version: Int,
        val message: String,
        @JsonProperty("logger_name")
        val loggerName: String,
        @JsonProperty("thread_name")
        val threadName: String,
        val level: Level,
        @JsonProperty("level_value")
        val levelValue: Long,
        @JsonProperty("HOSTNAME")
        val hostname: String,
        @JsonProperty("app")
        val appName: String
)


class MyLocalDateTimeSerializer : StdSerializer<LocalDateTime>(LocalDateTime::class.java) {
    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(value: LocalDateTime, gen: JsonGenerator, sp: SerializerProvider) {
        gen.writeString(value.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME  ))
    }
}


@NoArg
class Test(val id: Int)
