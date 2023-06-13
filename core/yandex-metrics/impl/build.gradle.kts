plugins {
    id("convention.android-library")
}

dependencies {
    api(project(Module.Core.YandexMetrics.api))

    implementation(project(Module.Core.common))

    implementation(libs.appMetrics)
}
