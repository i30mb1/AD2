plugins {
    id("bump-version-plugin")
    id("measure-build-plugin")
    id("convention.detekt")
    id("com.github.ben-manes.versions") version "0.44.0"
//    id("com.dorongold.task-tree") version "2.1.0" // adds a 'taskTree' task that prints task dependency tree
//    id("com.osacky.doctor") version "0.7.3"
//    id("com.autonomousapps.dependency-analysis") version "1.0.0-rc02"
    // https://arrow-kt.io/docs/meta/analysis/
}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
    group = "n7"
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}