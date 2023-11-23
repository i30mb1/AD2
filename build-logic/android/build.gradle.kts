plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.androidGradlePlugin)
    implementation(libs.kotlinGradlePlugin)

    implementation(projects.buildExtensions)
    implementation(projects.kotlin)
}
