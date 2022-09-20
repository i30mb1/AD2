@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.BaseExtension
import gradle.kotlin.dsl.accessors._4b7ad2363fc1fce7c774e054dc9a9300.implementation

configure<BaseExtension> {
    buildFeatures.compose = true
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    implementation(libs.bundles.compose)
}