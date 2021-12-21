plugins {
    androidLibrary()
    kapt()
}

dependencies {
    implementation(Lib.jsoup)
    implementation(Lib.exoPlayerCore)

    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.repositories))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.provider))
    implementation(project(Module.Core.spanParser))
    implementation(project(Module.Core.mediaPlayer))

    api(Lib.moshi)

    kapt(Lib.moshiCodegen)
    kapt(Lib.daggerAnnotation)
}