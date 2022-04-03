plugins {
    id("org.gradle.kotlin.kotlin-dsl")
}

gradlePlugin {
    plugins {
        register("extensions-plugin-registration") {
            implementationClass = "ExtensionsPlugin"
            id = "extensions"
        }
    }
}