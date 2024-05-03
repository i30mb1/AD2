plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
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

    api(libs.moshi)

    kapt(libs.moshiCodegen)
    kapt(libs.daggerAnnotation)

//    testImplementation(testFixtures(projects.core.appPreference)))
    testImplementation(testFixtures(projects.core.commonJvm))
    testImplementation(testFixtures(projects.core.logger.appLogger))
}
