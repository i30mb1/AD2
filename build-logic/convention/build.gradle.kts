plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(projects.kotlin)
    implementation(projects.android)

    implementation(libs.androidGradlePlugin)
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.dependencyGuard)
}
