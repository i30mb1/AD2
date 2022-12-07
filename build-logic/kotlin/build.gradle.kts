plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.androidGradlePlugin)
    implementation(libs.kotlinGradlePlugin)
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.22.0")
    implementation("org.jacoco:org.jacoco.core:0.8.8")
    implementation(project(":extensions"))
}