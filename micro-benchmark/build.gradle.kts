plugins {
    id("convention.android-library")
    id("androidx.benchmark")
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
    }
    testBuildType = "release"
}

dependencies {
    androidTestImplementation(libs.testRunner)
    androidTestImplementation(libs.testJunit)
    androidTestImplementation(libs.benchmarkJunit)
}