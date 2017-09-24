package com.example

import com.example.util.run
import org.hibernate.validator.constraints.NotBlank
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.reactive.function.client.WebClient
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid
import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.binder.JvmThreadMetrics
import io.micrometer.core.instrument.binder.ProcessorMetrics
import io.micrometer.core.instrument.binder.JvmGcMetrics


@SpringBootApplication
class UiApplication


//  register other binders with the registry
@Configuration
class MetricsConfiguration {
    @Bean
    fun threadMetrics(): JvmThreadMetrics {
        return JvmThreadMetrics();
    }

    @Bean
    fun processorMetrics(): ProcessorMetrics {
        return ProcessorMetrics()
    }

    @Bean
    fun gcMetrics(): JvmGcMetrics {
        return JvmGcMetrics()
    }
}

@Controller
@Timed
class MainController(@Value("\${app.cassandraApiUrl}") val cassandraApiUrl: String,
                     @Value("\${app.streamApiUrl}") val streamApiUrl: String) {

    @Timed(value = "list.books", extraTags = arrayOf("sumo", "ui-app" )) // used to further customize the timer
    @GetMapping("/")
	fun guestBook(model: Model): String {
		val entryList = WebClient.create(cassandraApiUrl)
				.get()
				.uri("api/guestbook")
				.accept(APPLICATION_JSON)
				.retrieve()
				.bodyToFlux(GuestBookEntryDTO::class.java)
				.log("uiapp-guestBook-get")

		model.addAttribute("entries", ReactiveDataDriverContextVariable(entryList, 1)) // buffers size = 1
		return "index"
	}

    @PostMapping("/guestbook")
    @ResponseBody
    fun postGuestBook(@Valid guestBookEntryVO: GuestBookEntryVO, result: BindingResult): Mono<GuestBookEntryDTO> {
        if (result.hasErrors()) {
            println(result.allErrors)
            throw Error("allErrors")
        }
        return WebClient.create(cassandraApiUrl)
                .post()
                .uri("api/guestbook")
                .accept(APPLICATION_JSON)
                .syncBody(guestBookEntryVO)
                .exchange()
                .flatMap { response -> response.bodyToMono(GuestBookEntryDTO::class.java) }
                .log("uiapp-guestBook-post")
    }

    @GetMapping(path = arrayOf("/guestbook/feed/{name}"), produces = arrayOf(TEXT_EVENT_STREAM_VALUE))
    @ResponseBody
    fun guestBookStream(@PathVariable("name") name: String): Flux<GuestBookEntryDTO> {
        return WebClient.create(cassandraApiUrl)
                .get()
                .uri("/sse/guestbook/$name")
                .accept(APPLICATION_STREAM_JSON)
                .retrieve()
                .bodyToFlux(GuestBookEntryDTO::class.java)
                .share()
                .log("uiapp-guestBook-feed")
    }

    @GetMapping(path = arrayOf("/guestbook/feed_html/{name}"), produces = arrayOf(TEXT_EVENT_STREAM_VALUE))
    fun guestBookHtmlStream(@PathVariable("name") name: String, model: Model): String {
        val guestBookStream = WebClient.create(cassandraApiUrl)
                .get()
                .uri("/sse/guestbook/$name")
                .accept(APPLICATION_STREAM_JSON)
                .retrieve()
                .bodyToFlux(GuestBookEntryDTO::class.java)
                .share()
                .log("uiapp-guestBook-feed_html")

        model.addAttribute("entries", ReactiveDataDriverContextVariable(guestBookStream, 1)) // buffers size = 1
        // Will use the same "guestbook" template, but only a fragment: the `entries` block.
        return "guestbook :: #entries"
    }

    @GetMapping("/quotes")
    fun quotes(): String {
        return "quotes"
    }

    @Timed(value = "quotes.feed", longTask = true)
    @GetMapping(path = arrayOf("/quotes/feed"), produces = arrayOf(TEXT_EVENT_STREAM_VALUE))
    @ResponseBody
    fun fetchQuotesStream(): Flux<Quote> {
        return WebClient.create(streamApiUrl)
                .get()
                .uri("/sse/quotes")
                .accept(APPLICATION_STREAM_JSON)
                .retrieve()
                .bodyToFlux(Quote::class.java)
                .share()
                .log("uiapp-quotes-feed")
    }

    @GetMapping("/logs")
    fun logs(): String {
        return "logs"
    }

    @Timed(value = "logs.feed", longTask = true)
    @GetMapping(path = arrayOf("/logs/feed"), produces = arrayOf(TEXT_EVENT_STREAM_VALUE))
    @ResponseBody
    fun fetchLogStream(): Flux<String> {
        return WebClient.create(streamApiUrl)
                .get()
                .uri("/sse/logs")
                .accept(APPLICATION_STREAM_JSON)
                .retrieve()
                .bodyToFlux(String::class.java)
                .share()
//                .log("uiapp-logs-feed")
    }
}

data class GuestBookEntryVO(
        val name: String,
        @NotBlank(message = "comment can't be empty!")
        val comment: String
)

fun main(args: Array<String>) {
    run(UiApplication::class, *args)
}


//@Bean
//open fun templateEngine(): SpringTemplateEngine {
//	val templateEngine = SpringTemplateEngine()
//	templateEngine.setTemplateResolver(templateResolver())
//	templateEngine.addDialect(SpringSecurityDialect())
//	templateEngine.addDialect(Java8TimeDialect())
//	return templateEngine
//}
