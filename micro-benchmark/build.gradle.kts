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
}
