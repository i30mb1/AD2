plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.detektApi)
    implementation(libs.detektCli)

    testImplementation(libs.detektTest)
    testImplementation(libs.bundles.test)
}