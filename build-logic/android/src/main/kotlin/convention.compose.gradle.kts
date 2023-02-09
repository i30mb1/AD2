@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.BaseExtension
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
//            "-P",
//            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" + buildDir.absolutePath + "\\compose_metrics",
//            "-P",
//            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" + buildDir.absolutePath + "\\compose_metrics",
        )
    }
}

dependencies {
    add("implementation", libs.bundles.compose)
}