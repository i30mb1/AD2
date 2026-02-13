plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.spotlessPlugin)
    implementation(libs.detektPlugin)
    implementation(libs.detektApi)
    implementation(libs.detektCli)

    implementation(projects.buildExtensions)

    testImplementation(libs.detektTest)
    testImplementation(libs.test.truth.jvm)
}
