plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    api(projects.feature.camera.domain.impl)
    api(projects.feature.camera.domain.api)

    implementation(projects.core.logger.appLogger)
    implementation(projects.core.commonJvm)
    implementation(projects.core.coroutines)

    implementation(projects.core.dagger)

    implementation(libs.lifecycle.common)
    ksp(libs.daggerAnnotation)
}
