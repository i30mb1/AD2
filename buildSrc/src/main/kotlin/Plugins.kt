import org.gradle.kotlin.dsl.kotlin
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

fun PluginDependenciesSpec.application(): PluginDependencySpec = id("convention.android-application")
fun PluginDependenciesSpec.androidLibrary(): PluginDependencySpec = id("convention.android-library")
fun PluginDependenciesSpec.kotlinLibrary(): PluginDependencySpec = id("convention.kotlin-jvm")
fun PluginDependenciesSpec.dynamicFeature(): PluginDependencySpec = id("com.android.dynamic-feature")
fun PluginDependenciesSpec.benchmark(): PluginDependencySpec = id("androidx.benchmark")
fun PluginDependenciesSpec.kapt(): PluginDependencySpec = kotlin("kapt")
fun PluginDependenciesSpec.junit5(): PluginDependencySpec = id("de.mannodermaus.android-junit5")