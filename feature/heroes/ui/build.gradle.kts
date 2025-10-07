plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)

    implementation(projects.feature.heroes.domain.api)

    ksp(libs.daggerAnnotation)

    androidTestImplementation(libs.test.fragment)
    androidTestImplementation(libs.test.junit.kotlin)
    androidTestImplementation(libs.test.mockk)
    androidTestImplementation(libs.test.espresso)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.coroutines)
    testImplementation(libs.test.truth.jvm)
    testImplementation(testFixtures(projects.core.logger.appLogger))
    testImplementation(testFixtures(projects.feature.heroes.domain.impl))
}
