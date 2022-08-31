plugins {
    id("convention.android-library")
    id("convention.jacoco")
    kotlin("kapt")
}

android {
    kotlin {
        explicitApi()
    }
}

dependencies {
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.android))

    testImplementation(libs.testTruth2)
    testImplementation(libs.mockitokotlin)

    kapt(libs.daggerAnnotation)
}