import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.detektPlugin)
    implementation(libs.detektApi)
    implementation(libs.detektCli)

    implementation(project(":extensions"))

    testImplementation(libs.detektTest)
    testImplementation(libs.testTruthJvm)
}
