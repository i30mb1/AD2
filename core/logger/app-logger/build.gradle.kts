plugins {
    id("convention.kotlin-jvm")
    id("java-test-fixtures")
}

dependencies {
    implementation(projects.core.coroutines)
    implementation(projects.core.commonJvm)

    testImplementation(libs.coroutinesDebug)
    testImplementation(libs.testTruthJvm)
    testImplementation(libs.coroutinesTest)

    testFixturesImplementation(libs.coroutines)
}
