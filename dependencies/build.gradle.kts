plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

gradlePlugin {
    plugins {
        register("dependencies-registration") {
            implementationClass = "DependenciesPlugin"
            id = "dependencies"
        }
    }
}