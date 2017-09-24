import com.palantir.gradle.docker.DockerExtension
import org.gradle.jvm.tasks.Jar

val testcontainersVersion by project

apply {
    plugin("com.palantir.docker")
}

val jar: Jar by tasks
docker {
    name = "${group}/${jar.baseName}:${jar.version}"
    files(jar.outputs)
    setDockerfile(file("src/main/docker/Dockerfile"))
    buildArgs(mapOf(
            "JAR_NAME" to jar.archiveName,
            "PORT"   to  "8080",
            "JAVA_OPTS" to "-Xms512m -Xmx1024m"
    ))
    pull(true)
    dependsOn(tasks.findByName("build"))
}

dependencies {
    compile(project(":shared"))

    compile("org.springframework.boot:spring-boot-starter-data-cassandra-reactive")
    // WHY: https://docs.datastax.com/en/developer/java-driver/3.3/manual/shaded_jar/
    compile("com.datastax.cassandra:cassandra-driver-core:3.3.0:shaded") {
        exclude("io.netty:*")
    }

    testCompile("org.testcontainers:testcontainers:$testcontainersVersion")
}

/**
 * Configures the [docker][DockerExtension] project extension.
 */
val Project.docker get() = extensions.getByName("docker") as DockerExtension

fun Project.docker(configure: DockerExtension.() -> Unit): Unit = extensions.configure("docker", configure)
