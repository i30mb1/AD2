plugins {
    id("convention.kotlin-jvm")
    id("convention.kotlin-serialization")
}

dependencies {
    api(libs.coroutines)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.truth.jvm)
}
