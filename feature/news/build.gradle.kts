plugins {
    id("convention.android-library")
    id("convention.compose")
    kotlin("kapt")
}

dependencies {
    implementation(libs.workManager)
    implementation(libs.composePaging)
    implementation(libs.jsoup)
    implementation(libs.webkit)

    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.provider))
    implementation(project(Module.Core.database))
    implementation(project(Module.Core.appPreference))

    kapt(libs.daggerAnnotation)
}