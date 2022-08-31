plugins {
    id("convention.kotlin-jvm")
    kotlin("kapt")
}

dependencies {
    implementation(project(Module.Core.dagger))
    kapt(libs.daggerAnnotation)

    api(libs.coroutines)
}