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
    androidTestImplementation(libs.testRunner)
    androidTestImplementation(libs.testJunitKtx)
    androidTestImplementation(libs.benchmarkJunit)
    androidTestImplementation(libs.coroutines)
}
