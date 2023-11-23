plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.detektPlugin)
    implementation(libs.detektApi)
    implementation(libs.detektCli)

    implementation(project(":build-extensions"))

    testImplementation(libs.detektTest)
    testImplementation(libs.testTruthJvm)
}
