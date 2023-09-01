import com.android.build.gradle.BaseExtension

plugins {
    kotlin("android")
}

configure<BaseExtension> {
    compileSdkVersion(33)

    buildFeatures.viewBinding = true

    defaultConfig {
        minSdk = 30
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lintOptions {
        isAbortOnError = false
        disable("UseCompoundDrawables")
    }
}

dependencies {
//    add("lintChecks", project(Module.Core.rules))
}
