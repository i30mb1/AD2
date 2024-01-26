plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(libs.moshi)

    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)

    implementation(projects.feature.heroes.domain.api)

    kapt(libs.daggerAnnotation)

    androidTestImplementation(libs.test.fragment)
    androidTestImplementation(libs.test.junit.kotlin)
    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.test.espresso)
}
