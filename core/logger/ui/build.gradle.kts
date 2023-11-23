plugins {
    id("convention.android-library")
    id("convention.compose")
}

dependencies {
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.commonJvm)
    implementation(projects.core.ui)
}
