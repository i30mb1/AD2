plugins {
    androidLibrary()
    kapt()
}

dependencies {
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.logger))
    kapt(libs.daggerAnnotation)

    api(libs.playCore)
    api(libs.playCoreKtx)
}