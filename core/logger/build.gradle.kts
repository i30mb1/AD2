plugins {
    id("convention.kotlin-jvm")
}

dependencies {
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.common))

    testImplementation(libs.bundles.test)
    testImplementation(libs.coroutinesDebug)
}