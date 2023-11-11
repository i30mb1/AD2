plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(project(Module.Feature.Xo.api))

    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.common))
    implementation(project(Module.Core.coroutines))

    implementation(libs.coroutines)

    testImplementation(libs.coroutinesDebug)
    testImplementation(libs.mockk)
    testImplementation(libs.testTruthJvm)
    testImplementation(libs.coroutinesTest)

    testFixturesImplementation(libs.coroutines)
}
