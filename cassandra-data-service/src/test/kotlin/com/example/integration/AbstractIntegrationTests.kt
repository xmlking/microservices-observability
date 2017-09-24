package com.example.integration


import com.example.repository.UserRepository
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.client.WebClient
import org.junit.ClassRule
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.*
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.GenericContainer
import java.io.File


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(PER_CLASS)
abstract class AbstractIntegrationTests {

    companion object {
        class KDockerComposeContainer(composeFile: File) : DockerComposeContainer<KDockerComposeContainer>(composeFile)

        @ClassRule @JvmField
        val  environment =  KDockerComposeContainer(File("src/test/resources/docker-compose.yml"))
            .withExposedService("zookeeper", 2181)
            .withExposedService("kafka", 9092)
            .withExposedService("cassandra", 9042)
    }

    @LocalServerPort
    var port: Int? = null

    lateinit var client: WebClient

    @BeforeAll
    fun setup() {
        client = WebClient.create("http://localhost:$port")
    }

    @AfterAll
    fun teardown() {
        println("tearDown")
    }

}

class CassandraContainer(version: String): GenericContainer<CassandraContainer>("cassandra:$version") {
    override fun start() {
        addFixedExposedPort(9042, 9042)
        super.start()
    }
}
