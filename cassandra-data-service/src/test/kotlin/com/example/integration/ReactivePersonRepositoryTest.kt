package com.example.integration

import com.example.domain.Role
import com.example.domain.User
import com.example.repository.UserRepository
import org.junit.Test
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Flux
import reactor.test.test

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ReactivePersonRepositoryTest(@LocalServerPort port: Int, @Autowired val repository: UserRepository) {

    @BeforeAll
    fun setup() {
        println("ReactivePersonRepository setup")
        repository.deleteAll()
            .thenMany(repository.insert(Flux.just(
                User("wwhite", "Walter", "White", 50, "wwhite@gmail.com", "Microsoft", Role.USER),
                User("skyler", "Skyler", "White", 45, "skyler@gmail.com", "Microsoft", Role.STAFF),
                User("sgoodman", "Saul", "Goodman", 42, "sgoodman@gmail.com", "Google", Role.USER),
                User("jpinkman", "Jesse", "Pinkman", 27, "jpinkman@gmail.com", "Facebook", Role.USER)
            )))
            .then()
            .block()
    }

    @AfterAll
    fun teardown() {
        println("ReactivePersonRepository tearDown")
    }

    @Test
    fun `Should insert and count data`() {

        val count = repository.count()

        val newCount = repository.insert(Flux.just(
            User("hschrader", "Hank", "Schrader", 43, "hschrader@gmail.com", "ebay", Role.USER),
            User("mehrmantraut", "Mike", "Ehrmantraut", 62, "mehrmantraut@gmail.com", "ebay", Role.USER)
        )).then( repository.count() )

        count.zipWith(newCount)
            .test()
            .consumeNextWith {
                println(it.t1)
                println(it.t2)
                it.t2 == it.t1 + 2L
            }
            .verifyComplete()
    }

    @Test
    fun `Should perform conversion before result processing`() {

        repository.findAll()
            .test()
            .expectNextCount(6L)
            .verifyComplete()
    }

    @Test
    fun `Should query data with query derivation`() {

        val whites = repository.findAllByCompany("Microsoft")
            .test()
            .expectNextCount(2L)
            .verifyComplete()
    }

    @Test
    fun `Should query data with string query`() {

        repository.findByLoginAndEmail("skyler", "skyler@gmail.com")
            .test()
            .consumeNextWith {
                assertEquals("skyler", it.login)
                assertEquals("skyler@gmail.com", it.email)
            }
            .verifyComplete()
    }


    @Test
    fun `Should query data by role`() {

        repository.findAllByRole(Role.STAFF)
            .test()
            .consumeNextWith {
                assertNull(it)
            }
            .verifyComplete()
    }


    @Test
    fun `Should query data with mixed deferred query derivation`() {

        repository.findByLoginAndRoleAndCompany("skyler", Role.STAFF, "Microsoft")
            .test()
            .consumeNextWith {
                assertNull(it)
            }
            .verifyComplete()
    }

}
