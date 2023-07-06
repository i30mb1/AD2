plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(libs.workManager)

    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.navigator))

    kapt(libs.daggerAnnotation)
}