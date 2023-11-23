plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(projects.core.dagger)
    implementation(projects.core.logger.appLogger)
    kapt(libs.daggerAnnotation)

    api(libs.playCoreKtx)
}
