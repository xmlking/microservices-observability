package com.example.web.handler

import com.example.domain.GuestBookEntry
import com.example.repository.GuestBookRepository
import com.example.util.json
import com.example.util.jsonStream
import com.example.util.textStream
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime

@Component
class GuestBookHandler(private val repository: GuestBookRepository) {


    fun findByNameOne(req: ServerRequest) = ok().json().body(repository.findByName(req.pathVariable("name")).take(1).single())

    fun findByName(req: ServerRequest) = ok().json().body(repository.findByName(req.pathVariable("name")))

    fun findAll(req: ServerRequest) = ok().json().body(repository.findAll())

    fun create(req: ServerRequest) = ok().json().body(repository.insert(req.bodyToMono<GuestBookEntry>()).single())

    fun fetchSSE(req: ServerRequest): Mono<ServerResponse> {

        val guestBookStream = Flux.interval(Duration.ofMillis(3000)).zipWith(
                repository.findByNameAndTimeAfter(req.pathVariable("name"),
                        LocalDateTime.now().minusSeconds(3)))
                .map { it.t2 }
                .log("GuestBookHandler 1")

        return ok().textStream().body(guestBookStream)
    }

    fun fetch(req: ServerRequest): Mono<ServerResponse> {

        val guestBookStream = Flux.interval(Duration.ofMillis(3000)).zipWith(
                repository.findByNameAndTimeAfter(req.pathVariable("name"),
                        LocalDateTime.now().minusSeconds(3)))
                .map { it.t2 }
                .log("GuestBookHandler 2")

        return ok().jsonStream().body(guestBookStream)

    }
}
