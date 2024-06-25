plugins {
    id("convention.android-library")
    id("convention.kotlin-serialization")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(libs.jsoup)

    implementation(projects.core.mediaPlayer)
    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.repositories)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)
    implementation(projects.core.spanParser)
    implementation(projects.core.mediaPlayer)

    kapt(libs.daggerAnnotation)
}
