plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}


dependencies {
    api(projects.core.database)
    implementation(projects.core.dagger)
    implementation(projects.core.commonAndroid)

    kapt(libs.daggerAnnotation)
}
// gradle app:dependencyInsight --configuration releaseRuntimeClasspath --dependency org.jetbrains.kotlin:kotlin-stdlib
