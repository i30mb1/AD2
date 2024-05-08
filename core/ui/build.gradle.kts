plugins {
    id("convention.android-library")
    id("convention.compose")
}

dependencies {
    implementation(libs.metrics)
    implementation(projects.core.coroutines)

    api(libs.appCompat)
    api(libs.splashScreen)
    api(libs.material)
    implementation(libs.fragment.compose)

    implementation(projects.core.ktx)
}
