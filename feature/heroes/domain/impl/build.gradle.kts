plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(projects.feature.heroes.domain.api)

    implementation(projects.core.commonJvm)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)

    implementation(libs.moshi)
    implementation(libs.room.ktx)

    kapt(libs.moshiCodegen)
    kapt(libs.room.compiler)
}
