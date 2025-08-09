plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    implementation(projects.core.dagger)
    implementation(projects.core.logger.appLogger)
    ksp(libs.daggerAnnotation)

    api(libs.playCoreKtx)
}
