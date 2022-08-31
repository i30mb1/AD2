@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.BaseExtension
import gradle.kotlin.dsl.accessors._6076749d6351b9fdce8c5a61387fb22f.implementation

configure<BaseExtension> {
    buildFeatures.compose = true
    composeOptions {
//        kotlinCompilerExtensionVersion = libs.versions.compose.get()
        kotlinCompilerExtensionVersion = "1.3.0"
    }
}

dependencies {
    implementation(libs.bundles.compose)
}