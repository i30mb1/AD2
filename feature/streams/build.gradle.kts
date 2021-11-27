plugins {
    androidLibrary()
    kotlinKapt()
    kotlinAndroid()
}

dependencies {
    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))

    api(Lib.retrofit)
    api(Lib.retrofitMoshiConverter)
    api(Lib.retrofitInterceptor)
    api(Lib.retrofitScalars)
    api(Lib.moshi)

    kapt(Lib.moshiCodegen)
    kapt(Lib.daggerAnnotation)
}
