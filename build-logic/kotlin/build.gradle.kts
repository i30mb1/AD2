plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.androidGradlePlugin)
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.detektPlugin)
    implementation("org.jacoco:org.jacoco.core:0.8.8")
    implementation(project(":extensions"))
}