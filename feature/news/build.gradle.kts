plugins {
    androidLibrary()
    kotlinAndroid()
    kotlinKapt()
}

dependencies {
    implementation(Lib.workManagerKotlin)

    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.provider))

    kapt(Lib.daggerAnnotation)
}