import com.android.build.gradle.BaseExtension

plugins {
    kotlin("android")
}

configure<BaseExtension> {
    compileSdkVersion(34)

    buildFeatures.viewBinding = true

    defaultConfig {
        minSdk = 31
        targetSdk = 34
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    add("lintChecks", project(":core:rules"))
}
