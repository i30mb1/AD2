plugins {
    id("convention.android-library")
    id("convention.compose")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    implementation(libs.playReview)

    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)

    ksp(libs.daggerAnnotation)
}
