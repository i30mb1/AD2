plugins {
    id("convention.kotlin-jvm")
    id("java-test-fixtures")
}

dependencies {
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.common))

    testImplementation(libs.coroutinesDebug)
    testImplementation(libs.testTruthJvm)
    testImplementation(libs.coroutinesTest)

    testFixturesImplementation(libs.coroutines)
}
