plugins {
    androidLibrary()
}

dependencies {
    api(libs.appCompat)
    api(libs.splashScreen)
    api(libs.material)

    implementation(project(Module.Core.ktx))
}