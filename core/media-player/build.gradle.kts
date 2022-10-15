plugins {
    id("convention.android-library")
    kotlin("kapt")
}

dependencies {
    api(libs.exoPlayer)

    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.android))
    kapt(libs.daggerAnnotation)
}