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

    testImplementation(libs.coroutinesDebug)
    testImplementation(libs.mockk)
    testImplementation(libs.testTruthJvm)
    testImplementation(libs.coroutinesTest)

    testFixturesImplementation(libs.coroutines)
}
