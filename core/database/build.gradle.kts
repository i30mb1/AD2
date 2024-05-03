plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(libs.room.ktx)

    implementation(libs.workManager)
    implementation(libs.moshi)
    implementation(libs.room.paging)

    implementation(projects.core.commonAndroid)
    implementation(projects.core.coroutines)
    implementation(projects.core.dagger)
    implementation(projects.core.logger.appLogger)

    kapt(libs.room.compiler)
    kapt(libs.moshiCodegen)
    kapt(libs.daggerAnnotation)
}
