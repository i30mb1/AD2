plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.androidGradlePlugin)
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.kotlinComposeCompilerPlugin)

    implementation(projects.buildExtensions)
    implementation(projects.kotlin)
}
