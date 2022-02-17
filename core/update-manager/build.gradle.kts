plugins {
    androidLibrary()
    kapt()
}

dependencies {
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.logger))
    kapt(Lib.daggerAnnotation)

    api(Lib.playCore)
    api(Lib.playCoreKtx)
}