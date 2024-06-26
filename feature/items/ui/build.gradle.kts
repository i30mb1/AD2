plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.repositories)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)

    implementation(projects.feature.items.domain.api)

    kapt(libs.daggerAnnotation)
}
