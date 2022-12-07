plugins {
    id("convention.kotlin-jvm")
}

dependencies {
    implementation(libs.detektApi)
    implementation(libs.detektCli)

    testImplementation(libs.detektTest)
    testImplementation(libs.bundles.test)
}