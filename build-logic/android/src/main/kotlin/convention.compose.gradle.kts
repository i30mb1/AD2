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
    /**
     * Generate file with info about compose functions
     * use Gradle task *:compileDebugKotlin
     */
    metricsDestination.set(File("${buildDir.absolutePath}\\compose_metrics"))
    reportsDestination.set(File("${buildDir.absolutePath}\\compose_metrics"))
}

dependencies {
    add("implementation", catalog.findBundle("compose").get())
}
