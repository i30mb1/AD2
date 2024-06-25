plugins {
    id("convention.android-library")
    id("convention.kotlin-serialization")
    id("convention.compose")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(libs.exoPlayerUI)
    implementation(libs.exoPlayerHLS)
    implementation(libs.exoPlayerSession)
    implementation(libs.composePaging)

    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.repositories)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)
    implementation(projects.core.retrofit)
    implementation(projects.core.mediaPlayer)

    kapt(libs.daggerAnnotation)
}
