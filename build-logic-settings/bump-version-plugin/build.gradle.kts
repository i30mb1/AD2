plugins {
    id("org.gradle.kotlin.kotlin-dsl")
}

gradlePlugin {
    plugins {
        register("bump-version-plugin-registration") {
            implementationClass = "BumpVersionPlugin"
            id = "bump-version-plugin"
        }
    }
}