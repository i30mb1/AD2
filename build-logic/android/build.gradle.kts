plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.androidGradlePlugin)
    implementation(libs.kotlinGradlePlugin)
    implementation(project(":extensions"))
    implementation(project(":kotlin"))
}