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

dependencies {
    // workaround for https://github.com/gradle/gradle/issues/15383
    api(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}