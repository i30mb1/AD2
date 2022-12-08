plugins {
    id("convention.android-library")
    kotlin("kapt")
}

dependencies {
    implementation(libs.appMetrics)

    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.common))

    kapt(libs.daggerAnnotation)
}