plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
}

android {
    testFixtures.enable = true
}

dependencies {
    implementation(libs.dataStorePref)

    implementation(projects.core.commonJvm)
    implementation(projects.core.dagger)

    ksp(libs.daggerAnnotation)
}
