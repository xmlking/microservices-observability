package com.example.integration

import com.example.domain.GuestBookEntry
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.test.test

class GuestBookApiIntegrationTests : AbstractIntegrationTests() {

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
