plugins {
    id("convention.android-library")
    kotlin("kapt")
}

dependencies {
    implementation(libs.dataStorePref)

    implementation(project(Module.Core.dagger))

    kapt(libs.daggerAnnotation)
}