package com.example.config

import com.datastax.driver.core.PlainTextAuthProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration
import org.springframework.data.cassandra.config.SchemaAction
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification
import org.springframework.data.cassandra.core.cql.keyspace.DropKeyspaceSpecification
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories


@Configuration
@EnableCassandraRepositories(basePackages = arrayOf("com.example.domain"))
class CassandraConfig(
        @Value("\${spring.data.cassandra.contact-points}") val _contactPoints: String,
        @Value("\${spring.data.cassandra.keyspace-name}") val _keyspaceName: String,
        @Value("\${spring.data.cassandra.create-keyspace:false}") val isCreateKeyspace: Boolean,
        @Value("\${spring.data.cassandra.schema-action}") val _schemaAction: String,
        @Value("\${spring.data.cassandra.username}") val username: String,
        @Value("\${spring.data.cassandra.password}") val password: String
) : AbstractCassandraConfiguration() {

    override fun getAuthProvider()= PlainTextAuthProvider(username, password)

    override fun getContactPoints() = _contactPoints

    override fun getKeyspaceName() = _keyspaceName

    override fun getSchemaAction() = SchemaAction.valueOf(_schemaAction)

    override fun getEntityBasePackages() = arrayOf("com.example.domain")

    override fun getKeyspaceCreations() =
            if(isCreateKeyspace)
                listOf(CreateKeyspaceSpecification.createKeyspace(keyspaceName).withSimpleReplication().ifNotExists())
            else
                super.getKeyspaceCreations()

    override fun getKeyspaceDrops() =
            if(isCreateKeyspace)
                listOf(DropKeyspaceSpecification.dropKeyspace(keyspaceName))
            else
                super.getKeyspaceDrops()

//    override fun getQueryOptions() =  with(QueryOptions()) {
//        consistencyLevel = ConsistencyLevel.LOCAL_QUORUM
//        this
//    }
//    override fun  getStartupScripts() = listOf("")
//    override fun  getShutdownScripts() = listOf("")
}
