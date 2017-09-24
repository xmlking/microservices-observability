import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.dsl.SpringBootExtension
import org.springframework.boot.gradle.tasks.run.BootRun

val logbackKafkaAppenderVersion by project
val logstashLogbackEncoderVersion by project
val kafkaVersion by project

buildscript {
    val springBootVersion = "2.0.0.M4"
    val junitGradleVersion = "1.0.0"

    repositories {
        mavenCentral()
        maven("https://repo.spring.io/milestone")
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
        classpath("org.junit.platform:junit-platform-gradle-plugin:$junitGradleVersion")
    }
}

plugins {
    val kotlinVersion = "1.1.50"
    val springDependencyManagement = "1.0.3.RELEASE"
    val springBootVersion = "2.0.0.RELEASE"
    val dockerPluginVersion = "0.13.0"

    base
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion apply false
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion apply false
    //TODO: id("org.springframework.boot") version springBootVersion apply false
    id("io.spring.dependency-management") version springDependencyManagement apply false
    id("com.palantir.docker") version dockerPluginVersion apply false
}

subprojects {

    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("org.junit.platform.gradle.plugin")
        plugin("org.jetbrains.kotlin.plugin.jpa")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
    }

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://repo.spring.io/snapshot")
        maven("https://repo.spring.io/milestone")
    }

    dependencies {
        // kotlin
        compile("org.jetbrains.kotlin:kotlin-stdlib-jre8")
        compile("org.jetbrains.kotlin:kotlin-reflect")
        // Web
        compile("org.springframework.boot:spring-boot-starter-webflux")
        // Testing
        testCompile("org.springframework.boot:spring-boot-starter-test") {
            exclude(module = "junit")
        }
        testCompile("org.junit.jupiter:junit-jupiter-api")
        testRuntime("org.junit.jupiter:junit-jupiter-engine")
        testCompile("io.projectreactor:reactor-test")
        // Logging
        runtime("com.github.danielwegener:logback-kafka-appender:$logbackKafkaAppenderVersion")
        runtime("net.logstash.logback:logstash-logback-encoder:$logstashLogbackEncoderVersion")
        // Tooling
        compileOnly("org.springframework:spring-context-indexer")
        compile("org.springframework.boot:spring-boot-devtools")
        compile("org.springframework.boot:spring-boot-starter-actuator")
        compile("io.micrometer:micrometer-registry-prometheus")
    }

    configurations.all {
        resolutionStrategy {
            force("org.apache.kafka:kafka-clients:$kafkaVersion")
        }
    }

    tasks {
        withType<KotlinCompile>().all {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = listOf("-Xjsr305=strict")
            }
        }

        withType<BootRun> {
            // Ensures IntelliJ can live reload resource files
            val sourceSets = the<JavaPluginConvention>().sourceSets
            sourceResources(sourceSets["main"])
        }
    }

    configure<SpringBootExtension> {
        buildInfo()
    }

}

