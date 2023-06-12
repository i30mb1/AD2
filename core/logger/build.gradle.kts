plugins {
    id("convention.kotlin-jvm")
    id("java-test-fixtures")
}

dependencies {
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.common))
    implementation(project(Module.Core.YandexMetrics.api))

    testImplementation(libs.bundles.test)
    testImplementation(libs.coroutinesDebug)

    testFixturesImplementation(libs.coroutines)
}