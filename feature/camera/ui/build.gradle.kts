plugins {
    id("convention.android-library")
    id("convention.compose")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(libs.bundles.camera)
    implementation(libs.bundles.kotlinDL)

    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)

    implementation(projects.feature.camera.domain.wiring)
    implementation(projects.feature.camera.domain.api)

    testImplementation(libs.coroutinesDebug)
    testImplementation(libs.testTruthJvm)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.test.lifecycle)

    androidTestImplementation(libs.test.lifecycle)
    androidTestImplementation(libs.coroutinesTest)
    androidTestImplementation(libs.test.junit)
    androidTestImplementation(libs.test.junit.kotlin)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(projects.feature.camera.domain.impl)
    androidTestImplementation(libs.testCoreKtx)
    androidTestImplementation(libs.testRules)

    kapt(libs.daggerAnnotation)
}
