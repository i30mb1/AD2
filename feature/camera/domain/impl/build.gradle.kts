plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(projects.feature.camera.domain.api)
    api(projects.core.logger.appLogger)
    api(projects.core.coroutines)
    api(libs.bundles.camera)

    implementation(projects.core.commonJvm)

    implementation(libs.bundles.kotlinDL)
    implementation(libs.activity)
    implementation(libs.coroutines)
    implementation(libs.lifecycle.common)
    implementation(libs.lifecycleRuntime)
    implementation(libs.litert)
    implementation(libs.litert.support)
    implementation(libs.litert.metadata)

    testImplementation(libs.test.mockk)
    testImplementation(libs.test.truth)
    testImplementation(libs.test.lifecycle)
    testImplementation(libs.test.robolectric)
    testImplementation(libs.test.junit.kotlin)
    testImplementation(libs.kotlin.reflection)

    androidTestImplementation(libs.test.lifecycle)
    androidTestImplementation(libs.test.coroutines)
    androidTestImplementation(libs.test.junit)
    androidTestImplementation(libs.test.junit.kotlin)
    androidTestImplementation(libs.test.runner)
}
