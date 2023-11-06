plugins {
    id("convention.android-library")
    id("convention.compose")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(libs.palette)

    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.navigator))
    implementation(project(Module.Core.repositories))
    implementation(project(Module.Core.nativeSecret))

    implementation(project(Module.Feature.Games.api))
    implementation(project(Module.Feature.Heroes.api))

    kapt(libs.daggerAnnotation)
}
