plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    implementation(projects.core.commonJvm)
    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)

    ksp(libs.daggerAnnotation)
}
