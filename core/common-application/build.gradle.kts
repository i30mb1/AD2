plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(projects.core.commonJvm)
    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)

    kapt(libs.daggerAnnotation)
}
