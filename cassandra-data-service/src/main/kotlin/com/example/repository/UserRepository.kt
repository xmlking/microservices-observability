package com.example.repository

import com.example.domain.Role
import reactor.core.publisher.Mono
import com.example.domain.User
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface UserRepository : ReactiveCassandraRepository<User, String> {

    fun findByLogin(login: String): Flux<User>
    @Query("SELECT * from user WHERE login = ?0 and email = ?1 ALLOW FILTERING")
//    @Query("{ 'login': ?0 and 'email' : ?1 }")
    fun findByLoginAndEmail(login: String, email: String): Mono<User>
    fun findByLoginAndRoleAndCompany(login: String, role: Role, company: String): Mono<User>

    @Query("SELECT * from user WHERE role = ?0 and company = ?1 ALLOW FILTERING")
//    @Query("{ 'role': ?0 and 'company' : ?1 }")
    fun findAllByRole(role: Role, company: String = "Microsoft" ): Flux<User>
    @Query("SELECT * from user WHERE company = ?0 ALLOW FILTERING")
    fun findAllByCompany(company: String): Flux<User>
}