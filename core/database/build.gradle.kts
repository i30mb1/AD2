plugins {
    androidLibrary()
    kapt()
}

dependencies {
    api(Lib.room)
    api(Lib.roomKtx)

    implementation(Lib.workManagerKotlin)
    implementation(Lib.moshi)

    implementation(project(Module.Core.android))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.logger))

    kapt(Lib.roomAnnotation)
    kapt(Lib.moshiCodegen)
    kapt(Lib.daggerAnnotation)

    addTestDependencies()
}