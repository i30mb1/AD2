plugins {
    id("convention.kotlin-jvm")
}

dependencies {
    implementation(libs.jsonSimple)
    implementation(libs.jsoup)
    implementation(libs.coroutines)
    testImplementation(libs.test.junit)
    testImplementation(libs.jsoup)
}
