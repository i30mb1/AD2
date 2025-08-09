plugins {
    id("convention.android-library")
    id("convention.kotlin-serialization")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    api(libs.room.ktx)

    implementation(libs.workManager)
    implementation(libs.room.paging)

    implementation(projects.core.commonAndroid)
    implementation(projects.core.coroutines)
    implementation(projects.core.dagger)
    implementation(projects.core.logger.appLogger)

    ksp(libs.room.compiler)
    ksp(libs.daggerAnnotation)
}
