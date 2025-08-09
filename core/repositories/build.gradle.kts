plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
}


dependencies {
    api(projects.core.database)
    implementation(projects.core.dagger)
    implementation(projects.core.commonAndroid)

    ksp(libs.daggerAnnotation)
}
// gradle app:dependencyInsight --configuration releaseRuntimeClasspath --dependency org.jetbrains.kotlin:kotlin-stdlib
