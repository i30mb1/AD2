plugins {
    dynamicFeature()
    kotlinAndroid()
}

android {
    defaultConfig {
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":app"))
    implementation(kotlin("stdlib-jdk7"))
}
