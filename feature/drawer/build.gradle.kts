plugins {
    androidLibrary()
    kapt()
}

dependencies {
    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.ui))
    implementation(project(Module.Core.provider))
    implementation(project(Module.Core.appPreference))

    api(Lib.retrofit)
    api(Lib.retrofitMoshiConverter)
    api(Lib.retrofitInterceptor)
    api(Lib.retrofitScalars)
    api(Lib.moshi)

    kapt(Lib.moshiCodegen)
    kapt(Lib.daggerAnnotation)
}
