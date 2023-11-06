plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(project(Module.Feature.Xo.impl))

    implementation(project(Module.Core.common))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.dagger))

    kapt(libs.daggerAnnotation)
}
