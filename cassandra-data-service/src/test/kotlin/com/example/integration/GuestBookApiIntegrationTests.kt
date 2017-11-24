package com.example.integration

import com.example.domain.GuestBookEntry
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.test.test

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GuestBookApiIntegrationTests(@LocalServerPort port: Int, @Autowired val client1: WebTestClient) {

    private val client = WebClient.create("http://localhost:$port")
    @Test
    fun `Find Sumo GuestBookEntry`() {
        client.get().uri("/api/guestbook/Sumo").accept(APPLICATION_JSON)
            .retrieve()
            .bodyToFlux<GuestBookEntry>()
            .test()
            .consumeNextWith {
                assertEquals("Sumo", it.name)
                assertEquals("comment0", it.comment)
            }
            .verifyComplete()
    }

    @Test
    fun `Find all GuestBookEntrys`() {
        client.get().uri("/api/guestbook").accept(APPLICATION_JSON)
            .retrieve()
            .bodyToFlux<GuestBookEntry>()
            .test()
            .expectNextCount(6)
            .verifyComplete()
    }

}
