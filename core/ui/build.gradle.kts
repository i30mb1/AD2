plugins {
    id("convention.android-library")
}

dependencies {
    implementation(libs.metrics)

    api(libs.appCompat)
    api(libs.splashScreen)
    api(libs.material)

    implementation(project(Module.Core.ktx))
}