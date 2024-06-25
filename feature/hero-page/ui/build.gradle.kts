plugins {
    id("convention.android-library")
    id("convention.kotlin-serialization")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(libs.workManager)
    implementation(libs.jsoup)
    implementation(libs.ticker)

    implementation(projects.core.mediaPlayer)
    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.repositories)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)
    implementation(projects.core.spanParser)
    implementation(projects.core.mediaPlayer)

    implementation(projects.feature.heroes.domain.api)
    implementation(projects.feature.heroPage.domain.api)
    kapt(libs.daggerAnnotation)

    testImplementation(libs.test.junit.kotlin)
    testImplementation(libs.test.robolectric)
    testImplementation(libs.test.fragment)
    testImplementation(libs.test.runner)
    testImplementation(libs.test.espresso)

    androidTestImplementation(libs.test.junit.kotlin)
    androidTestImplementation(libs.test.fragment)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.test.espresso)
    androidTestImplementation(libs.test.truth)
}
