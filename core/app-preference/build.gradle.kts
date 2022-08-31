plugins {
    id("convention.android-library")
    kotlin("kapt")
}

android {
    testFixtures.enable = true
}

dependencies {
    implementation(libs.dataStorePref)

    implementation(project(Module.Core.common))
    implementation(project(Module.Core.dagger))

    kapt(libs.daggerAnnotation)
}