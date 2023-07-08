plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(libs.moshi)

    implementation(project(Module.Core.common))
    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))

    kapt(libs.daggerAnnotation)
}
