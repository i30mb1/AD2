plugins {
    id("convention.android-library")
}

dependencies {
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.commonJvm)

    implementation(libs.androidStartup)

    implementation(libs.appMetrics)
}
