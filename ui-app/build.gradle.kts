import com.palantir.gradle.docker.DockerExtension
import org.gradle.jvm.tasks.Jar

val bootstrapVersion by project
val highchartsVersion by project
val xtermVersion by project
val webjarsLocatorVersion by project

apply {
    plugin("com.palantir.docker")
}

val jar: Jar by tasks
docker {
    name = "${group}/${jar.baseName}:${jar.version}"
    files(jar.outputs) //  jar.outputs , file("src/main/docker/.ssl/truststore.jks")
    setDockerfile(file("src/main/docker/Dockerfile"))
    buildArgs(mapOf(
            "JAR_NAME" to jar.archiveName,
            "PORT"   to  "8080",
            "JAVA_OPTS" to "-Xms64m -Xmx128m"
    ))
    pull(true)
    dependsOn(tasks.findByName("build"))
}

dependencies {
    compile(project(":shared"))

    compile("org.springframework.boot:spring-boot-starter-thymeleaf")
//    compile("org.thymeleaf.extras:thymeleaf-extras-java8time")
    runtime("org.webjars:webjars-locator:$webjarsLocatorVersion")
    runtime("org.webjars:bootstrap:$bootstrapVersion")
    runtime("org.webjars:highcharts:$highchartsVersion")
    runtime("org.webjars.npm:xterm:$xtermVersion")

    compile("com.fasterxml.jackson.module:jackson-module-kotlin")
    compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}

/**
 * Configures the [docker][DockerExtension] project extension.
 */
val Project.docker get() = extensions.getByName("docker") as DockerExtension

fun Project.docker(configure: DockerExtension.() -> Unit): Unit = extensions.configure("docker", configure)
