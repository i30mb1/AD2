plugins {
    id("convention.android-library")
    id("convention.compose")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(libs.exoPlayerUI)
    implementation(libs.exoPlayerHLS)
    implementation(libs.exoPlayerSession)
    implementation(libs.composePaging)

    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.repositories))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.provider))
    implementation(project(Module.Core.retrofit))
    implementation(project(Module.Core.mediaPlayer))

    api(libs.moshi)

    kapt(libs.moshiCodegen)
    kapt(libs.daggerAnnotation)
}
