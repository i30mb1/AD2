plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

android {
    testFixtures.enable = true
}

dependencies {
    implementation(libs.dataStorePref)

    implementation(projects.core.commonJvm)
    implementation(projects.core.dagger)

    kapt(libs.daggerAnnotation)
}
