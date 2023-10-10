import com.android.build.gradle.BaseExtension

plugins {
    kotlin("android")
}

configure<BaseExtension> {
    compileSdkVersion(34)

    buildFeatures.viewBinding = true

    defaultConfig {
        minSdk = 30
        targetSdk = 34
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
