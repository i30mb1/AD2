plugins {
    dynamicFeature()
    kotlinAndroid()
}

dependencies {
    implementation(project(":app"))
    implementation(kotlin("stdlib-jdk7"))
}
