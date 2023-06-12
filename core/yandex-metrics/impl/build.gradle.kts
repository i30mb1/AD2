plugins {
    id("convention.android-library")
}

dependencies {
    api(project(Module.Core.YandexMetrics.api))

    implementation(libs.appMetrics)

    implementation(project(Module.Core.common))
}
