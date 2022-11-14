@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.BaseExtension
import gradle.kotlin.dsl.accessors._4b7ad2363fc1fce7c774e054dc9a9300.implementation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

configure<BaseExtension> {
    buildFeatures.compose = true
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" + buildDir.absolutePath + "\\compose_metrics",
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" + buildDir.absolutePath + "\\compose_metrics",
        )
    }
}

dependencies {
    implementation(libs.bundles.compose)
}