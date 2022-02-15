plugins {
    kotlinLibrary()
    kapt()
}

dependencies {
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.logger))

    api(Lib.retrofit)
    api(Lib.retrofitMoshiConverter)
    api(Lib.retrofitInterceptor)
    api(Lib.retrofitScalars)

    kapt(Lib.daggerAnnotation)
}