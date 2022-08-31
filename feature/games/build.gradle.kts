plugins {
    id("convention.android-library")
    kotlin("kapt")
}

dependencies {
    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.provider))

    kapt(libs.daggerAnnotation)
}