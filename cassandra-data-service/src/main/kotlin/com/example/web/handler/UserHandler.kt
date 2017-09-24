package com.example.web.handler

import com.example.domain.*
import com.example.repository.UserRepository
import com.example.util.*
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.net.URI.*
import java.net.URLDecoder

@Component
class UserHandler(private val repository: UserRepository) {

    fun findOneView(req: ServerRequest) =
            repository.findByLogin(URLDecoder.decode(req.pathVariable("login"), "UTF-8")).flatMap {
                ok().render("user", mapOf(Pair("user", it.toDto())))
            }

    fun findOne(req: ServerRequest) = ok().json().body(repository.findByLogin(req.pathVariable("login")))
        .switchIfEmpty(ServerResponse.notFound().build())

    fun findById(req: ServerRequest): Mono<ServerResponse> {
        val notFound = ServerResponse.notFound().build()
        val id = req.pathVariable("id");
        return repository.findById(id).flatMap { user -> ServerResponse.ok().body(Mono.just(user)) }
            .switchIfEmpty(notFound)
    }

    fun findAll(req: ServerRequest) = ok().json().body(repository.findAll())
    fun findAllStaff(req: ServerRequest) = ok().json().body(repository.findAllByRole(Role.STAFF))
    fun findOneStaff(req: ServerRequest) = ok().json().body(repository.findByLoginAndRoleAndCompany(req.pathVariable("login"), Role.STAFF, "Microsoft"))

    fun create0(req: ServerRequest) = repository.insert(req.bodyToMono<User>()).elementAt(0).map {
        created(create("/api/user/${it.login}")).json().body(it.toMono())
    }

    fun create(req: ServerRequest) = created(create("/api/user/xxx")).json().body(repository.insert(req.bodyToMono<User>()).elementAt(0))

}

class UserDto(
        val login: String,
        val firstname: String,
        val lastname: String,
        val age: Int,
        var email: String,
        var company: String? = null,
        var emailHash: String? = null,
        var photoUrl: String? = null,
        val role: Role,
        val logoType: String?,
        val logoWebpUrl: String? = null
)

fun User.toDto() =
        UserDto(login, firstname, lastname, age, email, company,
                emailHash, photoUrl, role,  logoType(photoUrl), logoWebpUrl(photoUrl))

private fun logoWebpUrl(url: String?) =
        when {
            url == null -> null
            url.endsWith("png") -> url.replace("png", "webp")
            url.endsWith("jpg") -> url.replace("jpg", "webp")
            else -> null
        }

private fun logoType(url: String?) =
        when {
            url == null -> null
            url.endsWith("svg") -> "image/svg+xml"
            url.endsWith("png") -> "image/png"
            url.endsWith("jpg") -> "image/jpeg"
            else -> null
        }
