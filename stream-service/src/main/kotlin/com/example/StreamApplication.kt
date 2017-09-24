package com.example

import com.example.util.run
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_STREAM_JSON
import org.springframework.http.MediaType.TEXT_EVENT_STREAM
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux
import reactor.core.publisher.SynchronousSink
import reactor.core.publisher.toFlux
import java.math.BigDecimal
import java.math.MathContext
import java.time.Duration
import java.time.Duration.ofMillis
import java.time.Instant
import java.util.*
import kotlin.coroutines.experimental.buildIterator
import reactor.kafka.receiver.KafkaReceiver
//import org.springframework.context.support.*
import org.slf4j.MDC

@SpringBootApplication
class StreamApplication

private val log = LoggerFactory.getLogger(StreamApplication::class.java)

// TODO: Kotlin Functional bean declaration DSL
// https://stackoverflow.com/questions/45935931/how-to-use-functional-bean-definition-kotlin-dsl-with-spring-boot-and-spring-w/46033685#46033685
//fun beans() = beans {
//    bean {
//        QuoteRoutes(it.ref())
//    }
//}


@Configuration
class QuoteRoutes(private val streamHandler: StreamHandler) {
    @Bean
    fun quoteRouter() = router {
        GET("/sse/quotes").nest {
            accept(TEXT_EVENT_STREAM, streamHandler::fetchQuotesSSE)
            accept(APPLICATION_STREAM_JSON, streamHandler::fetchQuotes)
        }
        GET("/sse/fibonacci").nest {
            accept(TEXT_EVENT_STREAM, streamHandler::fetchFibonacciSSE)
            accept(APPLICATION_STREAM_JSON, streamHandler::fetchFibonacci)
        }
        GET("/sse/logs").nest {
            accept(TEXT_EVENT_STREAM, streamHandler::fetchLogsSSE)
            accept(APPLICATION_STREAM_JSON, streamHandler::fetchLogs)
        }
    }

}

@Component
class StreamHandler(private val quoteGenerator: QuoteGenerator,
                    private val kafkaDataReceiver: KafkaReceiver<String, String>,
                    private val objectMapper: ObjectMapper) {
    final val quoteStream = quoteGenerator.fetchQuoteStream(ofMillis(200)).share()
    final val fibonacciStream= quoteGenerator.fetchFibonacciStream(ofMillis(1000)).share();
    final val logStream = kafkaDataReceiver.receive().map { objectMapper.readValue(it.value(), Log::class.java) }.share()

    init{
        logStream.subscribe {
                //TODO: fake subscriber to keep kafka connect active
        }
    }
    fun fetchQuotesSSE(req: ServerRequest) = ok()
            .contentType(TEXT_EVENT_STREAM)
            .body(quoteStream, Quote::class.java)

    fun fetchQuotes(req: ServerRequest) = ok()
            .contentType(APPLICATION_STREAM_JSON)
            .body(quoteStream, Quote::class.java)

    fun fetchFibonacciSSE(req: ServerRequest) = ok()
            .contentType(TEXT_EVENT_STREAM)
            .body(fibonacciStream, String::class.java)

    fun fetchFibonacci(req: ServerRequest) = ok()
            .contentType(APPLICATION_STREAM_JSON)
            .body(fibonacciStream, String::class.java)

    fun fetchLogsSSE(req: ServerRequest) = ok()
            .contentType(TEXT_EVENT_STREAM)
            .body(logStream, Log::class.java)

    fun fetchLogs(req: ServerRequest) = ok()
            .contentType(APPLICATION_STREAM_JSON)
            .body(logStream, Log::class.java)
}

@Service
class QuoteGenerator {

    val mathContext = MathContext(2)

    val random = Random()

    val prices = listOf(
            Quote("CTXS", BigDecimal(82.26, mathContext)),
            Quote("DELL", BigDecimal(63.74, mathContext)),
            Quote("GOOG", BigDecimal(847.24, mathContext)),
            Quote("MSFT", BigDecimal(65.11, mathContext)),
            Quote("ORCL", BigDecimal(45.71, mathContext)),
            Quote("RHT", BigDecimal(84.29, mathContext)),
            Quote("VMW", BigDecimal(92.21, mathContext))
    )


    fun fetchQuoteStream(period: Duration) = Flux.generate({ 0 },
            { index, sink: SynchronousSink<Quote> ->
                sink.next(updateQuote(prices[index]))
                (index + 1) % prices.size
            }).zipWith(Flux.interval(period))
            .map { it.t1.copy(instant = Instant.now()) }
            .log("ss-QuoteGenerator")


    private fun updateQuote(quote: Quote) = quote.copy(
            price = quote.price.add(quote.price.multiply(
                    BigDecimal(0.05 * random.nextDouble()), mathContext))
    )


    val fibonacci= buildIterator {

        var a = 0L
        var b = 1L

        while (true) {
            yield(b)

            val next = a + b
            a = b; b = next
        }
    }

    fun fetchFibonacciStream(interval: Duration) = fibonacci.toFlux()
            .delayElements(interval)
            .map{it.toString()}
            .log("ss-fibonacci")

}


// NOTE: declared it in shared module `commons`
//data class Quote(val ticker: String, val price: BigDecimal, val instant: Instant = Instant.now())

fun main(args: Array<String>) {
    run(StreamApplication::class, *args)
//    SpringApplication(Application::class.java).apply {
//        addInitializers(beans())
//        run(*args)
//    }
}


// Programmatic bootstrap - Start without spring boot
//fun main(args: Array<String>) {
//    val quoteGenerator =  QuoteGenerator();
//    val quoteHandler =  QuoteHandler(quoteGenerator);
//    val routes = QuoteRoutes(quoteHandler).quoteRouter()
//
//    val handler = ReactorHttpHandlerAdapter(RouterFunctions.toHttpHandler(routes))
//    HttpServer.create(8082).newHandler(handler).block()
//
//    Thread.currentThread().join()
//}
