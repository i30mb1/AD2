plugins {
    id("convention.android-library")
    id("convention.kotlin-serialization")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(libs.room.ktx)

    implementation(libs.workManager)
    implementation(libs.room.paging)

    implementation(projects.core.commonAndroid)
    implementation(projects.core.coroutines)
    implementation(projects.core.dagger)
    implementation(projects.core.logger.appLogger)

    kapt(libs.room.compiler)
    kapt(libs.daggerAnnotation)
}
