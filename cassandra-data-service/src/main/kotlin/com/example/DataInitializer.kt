package com.example

import com.example.domain.GuestBookEntry
import com.example.repository.GuestBookRepository
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class DataInitializer(val repository: GuestBookRepository) {

    @EventListener(ContextRefreshedEvent::class)
    fun init()  {
        initGuestBookEntrys();
        initUsers();
    }

    private fun initGuestBookEntrys() {
        if(repository.count().block() == 0L) {
            val entries = Flux.just(
                GuestBookEntry("Sumo", "comment0"),
                GuestBookEntry("Jack Bauer", "comment1"),
                GuestBookEntry("Chloe O'Brian", "comment2"),
                GuestBookEntry("Kim Bauer", "comment3"),
                GuestBookEntry("David Palmer", "comment4"),
                GuestBookEntry("Michelle Dessler", "comment5")
            )
            repository.insert(entries).blockLast()
            println("created test data: ${repository.count().block()}")
        }
    }

    private fun initUsers() {

    }
}
