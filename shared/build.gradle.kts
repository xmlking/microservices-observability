import org.gradle.api.tasks.bundling.Jar
import org.springframework.boot.gradle.tasks.bundling.BootJar

// Prevents common subproject dependencies from being included in the common jar itself.
// Without this, each subproject that included common would include each shared dependency twice.
//tasks {
//    withType<BootJar> {
//        enabled = false
//    }
//    withType<Jar> {
//        enabled = true
//    }
//}
val bootJar: BootJar by tasks
bootJar.enabled = false
val jar: Jar by tasks
jar.enabled = true
