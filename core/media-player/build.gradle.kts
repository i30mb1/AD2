plugins {
    androidLibrary()
    kapt()
}

dependencies {
    implementation(libs.exoPlayerCore)

    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.android))
    kapt(libs.daggerAnnotation)
}