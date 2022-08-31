plugins {
    id("convention.android-library")
    kotlin("kapt")
}

dependencies {
    implementation(libs.exoPlayerCore)

    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.android))
    kapt(libs.daggerAnnotation)
}