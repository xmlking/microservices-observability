package com.example.repository

import com.example.domain.GuestBookEntry
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

@Repository
public interface GuestBookRepository : ReactiveCassandraRepository<GuestBookEntry, String> {

    fun findByName(name: String): Flux<GuestBookEntry>
    fun findByNameAndTime(name: String, time: LocalDateTime): Flux<GuestBookEntry>

    // @Tailable fun findWithTailableCursorBy(): Flux<GuestBookEntry> // Use a tailable cursor???
    fun findByNameAndTimeAfter(name: String, time: LocalDateTime): Flux<GuestBookEntry>
}