plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("bump-version-plugin-registration") {
            implementationClass = "BumpVersionPlugin"
            id = "bump-version-plugin"
            version = "1.0.0"
        }
    }
}
