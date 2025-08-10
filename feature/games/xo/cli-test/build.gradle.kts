plugins {
    id("convention.kotlin-jvm")
}

dependencies {
    implementation(projects.core.coroutines)
    implementation(projects.core.commonJvm)

    implementation(libs.coroutines)
}
