plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    api(libs.exoPlayer)

    implementation(projects.core.dagger)
    implementation(projects.core.commonAndroid)
    ksp(libs.daggerAnnotation)
}
