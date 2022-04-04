plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("extensions-plugin-registration") {
            implementationClass = "ExtensionsPlugin"
            id = "extensions"
        }
    }
}