plugins {
    androidLibrary()
    kotlinAndroid()
    kotlinKapt()
}

dependencies {
    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.ui))

    api(Lib.retrofit)
    api(Lib.retrofitMoshiConverter)
    api(Lib.retrofitInterceptor)
    api(Lib.retrofitScalars)
    api(Lib.moshi)

    kapt(Lib.moshiCodegen)
    kapt(Lib.daggerAnnotation)
}