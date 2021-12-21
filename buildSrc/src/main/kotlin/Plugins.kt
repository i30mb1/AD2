import org.gradle.kotlin.dsl.kotlin
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

fun PluginDependenciesSpec.dynamicFeature(): PluginDependencySpec = id("com.android.dynamic-feature")

fun PluginDependenciesSpec.androidApp(): PluginDependencySpec = id("android-application-convention")

fun PluginDependenciesSpec.junit5(): PluginDependencySpec = id("de.mannodermaus.android-junit5")

fun PluginDependenciesSpec.kotlinKapt(): PluginDependencySpec = kotlin("kapt")

fun PluginDependenciesSpec.javaLibrary(): PluginDependencySpec = id("java-library")

fun PluginDependenciesSpec.androidLibrary(): PluginDependencySpec = id("android-library-convention")

fun PluginDependenciesSpec.kotlinLibrary(): PluginDependencySpec = id("kotlin-jvm-convention")