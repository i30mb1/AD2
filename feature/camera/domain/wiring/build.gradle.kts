plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(projects.feature.camera.domain.api)

    implementation(projects.core.logger.appLogger)
    implementation(projects.core.commonJvm)
    implementation(projects.core.coroutines)

    kapt(libs.daggerAnnotation)
}
