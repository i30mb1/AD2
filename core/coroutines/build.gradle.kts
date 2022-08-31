plugins {
    id("convention.kotlin-jvm")
    kotlin("kapt")
}

dependencies {
    api(libs.coroutines)
    implementation(project(Module.Core.dagger))
    implementation(libs.bundles.test)

    kapt(libs.daggerAnnotation)
}