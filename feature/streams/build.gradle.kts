plugins {
    androidLibrary()
    kapt()
}

dependencies {
    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.repositories))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.provider))
    implementation(project(Module.Core.retrofit))

    api(libs.moshi)

    kapt(libs.moshiCodegen)
    kapt(libs.daggerAnnotation)
}
