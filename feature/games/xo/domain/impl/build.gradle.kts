plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(projects.feature.games.xo.domain.api)

    implementation(projects.core.logger.appLogger)
    implementation(projects.core.commonJvm)
    implementation(projects.core.coroutines)

    implementation(libs.coroutines)
    implementation(libs.coreKtx)

    testImplementation(libs.test.coroutines)
    testImplementation(libs.test.coroutines.debug)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.truth.jvm)
}
