@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.BaseExtension
import gradle.kotlin.dsl.accessors._44fb0a05fcd9e15986a76c748cb18b72.implementation

configure<BaseExtension> {
    buildFeatures.compose = true
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
}

dependencies {
    implementation(libs.bundles.compose)
}