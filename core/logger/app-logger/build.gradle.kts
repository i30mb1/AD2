plugins {
    id("convention.kotlin-jvm")
    id("java-test-fixtures")
}

dependencies {
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.common))

    testImplementation(libs.bundles.test)
    testImplementation(libs.coroutinesDebug)

    testFixturesImplementation(libs.coroutines)
}