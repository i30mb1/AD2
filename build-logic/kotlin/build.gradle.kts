plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.androidGradlePlugin)
    implementation(libs.kotlinGradlePlugin)
    implementation("com.github.arturbosch:detekt:1.22.0-RC3")
    implementation("org.jacoco:org.jacoco.core:0.8.8")
    implementation(project(":extensions"))
}