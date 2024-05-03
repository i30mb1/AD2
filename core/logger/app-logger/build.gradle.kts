plugins {
    id("convention.kotlin-jvm")
    id("java-test-fixtures")
}

dependencies {
    implementation(projects.core.coroutines)
    implementation(projects.core.commonJvm)

    testImplementation(libs.test.coroutines)
    testImplementation(libs.test.coroutines.debug)
    testImplementation(libs.test.truth.jvm)

    testFixturesImplementation(libs.coroutines)
}
