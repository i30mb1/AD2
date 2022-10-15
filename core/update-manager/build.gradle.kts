plugins {
    id("convention.android-library")
    kotlin("kapt")
}

dependencies {
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.logger))
    kapt(libs.daggerAnnotation)

    api(libs.playCoreKtx)
}