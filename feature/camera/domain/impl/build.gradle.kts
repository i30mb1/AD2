plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(projects.feature.camera.domain.api)

    implementation(projects.core.logger.appLogger)
    implementation(projects.core.commonJvm)
    implementation(projects.core.coroutines)

    implementation(libs.bundles.camera)
    implementation(libs.bundles.kotlinDL)
    implementation(libs.activity)
    implementation(libs.coroutines)
    implementation(libs.lifecycle.common)
    implementation(libs.lifecycleRuntime)

    testImplementation(libs.mockk)
    testImplementation(libs.testTruthJvm)
    testImplementation(libs.test.lifecycle)
    testImplementation(libs.test.robolectric)
    testImplementation(libs.test.junit.kotlin)
    testImplementation(libs.kotlin.reflection)

    androidTestImplementation(libs.test.lifecycle)
    androidTestImplementation(libs.coroutinesTest)
    androidTestImplementation(libs.test.junit)
    androidTestImplementation(libs.test.junit.kotlin)
    androidTestImplementation(libs.test.runner)
}
