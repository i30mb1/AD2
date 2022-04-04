plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("measure-build-plugin-registration") {
            implementationClass = "MeasureBuildPlugin"
            id = "measure-build-plugin"
        }
    }
}