plugins {
    id("convention.android-library")
    id("convention.compose")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)

    implementation(projects.feature.camera.domain.api)

    kapt(libs.daggerAnnotation)
}
