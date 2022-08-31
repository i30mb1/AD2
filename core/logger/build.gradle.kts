plugins {
    id("convention.kotlin-jvm")
}

dependencies {
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))

    testImplementation(libs.bundles.test)
}