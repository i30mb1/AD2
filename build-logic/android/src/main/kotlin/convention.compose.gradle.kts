@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

plugins {
    id("org.jetbrains.kotlin.plugin.compose")
}

configure<BaseExtension> {
    buildFeatures.compose = true
}

configure<ComposeCompilerGradlePluginExtension> {
    enableStrongSkippingMode = true
    includeSourceInformation = true
    enableNonSkippingGroupOptimization = true
    val file = layout.projectDirectory.file("stability_config.conf")
    if (file.asFile.exists()) {
        stabilityConfigurationFile.set(file)
    }
    reportsDestination = layout.buildDirectory.dir("compose_compiler")

    /**
     * Generate file with info about compose functions
     * use Gradle task *:compile****Kotlin --rerun-tasks
     */
    metricsDestination.set(layout.buildDirectory.dir("compose_metrics"))
    reportsDestination.set(layout.buildDirectory.dir("compose_metrics"))
}

dependencies {
    add("implementation", catalog.findBundle("compose").get())
}
