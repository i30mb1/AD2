plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.repositories))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.provider)) {

    }

    api(libs.moshi)

    kapt(libs.daggerAnnotation)
}