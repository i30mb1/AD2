plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(libs.exoPlayer)

    implementation(projects.core.dagger)
    implementation(projects.core.commonAndroid)
    kapt(libs.daggerAnnotation)
}
