plugins {
    id("convention.android-library")
    alias(libs.plugins.benchmark)
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
    }
    testBuildType = "release"
}

dependencies {
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.test.junit.kotlin)
    androidTestImplementation(libs.benchmarkJunit)
    androidTestImplementation(libs.test.coroutines)
    androidTestImplementation(libs.test.lifecycle)
    androidTestImplementation(libs.test.rules)

    androidTestImplementation(projects.feature.camera.domain.impl)
}
