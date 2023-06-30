plugins {
    id("convention.android-library")
}

dependencies {
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.common))

    implementation(libs.appMetrics)
}
