plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(libs.moshi)

    implementation(projects.core.commonJvm)
    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)

    kapt(libs.daggerAnnotation)
}
