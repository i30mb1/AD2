plugins {
    id("convention.android-library")
    id("convention.kotlin-serialization")
    id("n7.plugins.kotlin-ksp")
}

android {
    testFixtures.enable = true
}

dependencies {
    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.ui)
    implementation(projects.core.navigator)
    implementation(projects.core.appPreference)
    implementation(projects.core.retrofit)

    ksp(libs.daggerAnnotation)

    testImplementation(testFixtures(projects.core.appPreference))
    testImplementation(testFixtures(projects.core.commonJvm))
    testImplementation(testFixtures(projects.core.logger.appLogger))
    testImplementation(libs.test.junit)
    testImplementation(libs.test.truth)
}
