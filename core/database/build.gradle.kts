plugins {
    androidLibrary()
    kapt()
}

dependencies {
    api(libs.room)
    api(libs.roomKtx)

    implementation(libs.workManager)
    implementation(libs.moshi)

    implementation(project(Module.Core.android))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.logger))

    kapt(libs.roomAnnotation)
    kapt(libs.moshiCodegen)
    kapt(libs.daggerAnnotation)
}