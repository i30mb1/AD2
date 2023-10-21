plugins {
    id("convention.android-library")
    id("convention.compose")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(libs.workManager)
    implementation(libs.composePaging)
    implementation(libs.webkit)

    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.navigator))
    implementation(project(Module.Core.database))
    implementation(project(Module.Core.appPreference))

    implementation(project(Module.Feature.News.api))

    kapt(libs.daggerAnnotation)
}