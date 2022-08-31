plugins {
    id("convention.android-library")
}

dependencies {
    api(libs.appCompat)
    api(libs.splashScreen)
    api(libs.material)

    implementation(project(Module.Core.ktx))
}