plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(project(Module.Feature.Heroes.impl))

    implementation(project(Module.Core.common))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.logger))

    implementation(libs.moshi)

    kapt(libs.daggerAnnotation)
}
