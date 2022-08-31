import com.android.build.gradle.BaseExtension
import gradle.kotlin.dsl.accessors._44fb0a05fcd9e15986a76c748cb18b72.lintChecks

plugins {
    kotlin("android")
}

configure<BaseExtension> {
    compileSdkVersion(33)

    buildFeatures.viewBinding = true

    defaultConfig {
        minSdk = 23
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lintOptions {
        isAbortOnError = false
        disable("UseCompoundDrawables")
    }
}

dependencies {
    lintChecks(project(Module.Core.rules))
}