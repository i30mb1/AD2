plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(projects.feature.camera.domain.impl)
    implementation(projects.feature.camera.domain.api)

    implementation(projects.core.logger.appLogger)
    implementation(projects.core.commonJvm)
    implementation(projects.core.coroutines)

    implementation(projects.core.dagger)

    implementation(libs.lifecycle.common)
    kapt(libs.daggerAnnotation)
}
