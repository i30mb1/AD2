import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}