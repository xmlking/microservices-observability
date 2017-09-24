package com.example.domain

import org.springframework.data.cassandra.core.cql.PrimaryKeyType.*
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("user")
data class User(
        @PrimaryKeyColumn(type =  PARTITIONED, ordinal = 1)
        val login: String,
        val firstname: String,
        val lastname: String,
        val age: Int,
        @Indexed()
        val email: String,
        @PrimaryKeyColumn(type =  CLUSTERED, ordinal = 2)
        val company: String,
        @PrimaryKeyColumn(type =  CLUSTERED, ordinal = 3)
        val role: Role = Role.USER,
        val emailHash: String? = null,
        val photoUrl: String? = null
)

enum class Role {
    STAFF,
    USER
}