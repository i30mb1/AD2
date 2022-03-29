import org.gradle.kotlin.dsl.kotlin
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

fun PluginDependenciesSpec.application(): PluginDependencySpec = id("android-application-convention")
fun PluginDependenciesSpec.androidLibrary(): PluginDependencySpec = id("android-library-convention")
fun PluginDependenciesSpec.kotlinLibrary(): PluginDependencySpec = id("convention.kotlin-jvm")
fun PluginDependenciesSpec.dynamicFeature(): PluginDependencySpec = id("com.android.dynamic-feature")
fun PluginDependenciesSpec.benchmark(): PluginDependencySpec = id("androidx.benchmark")
fun PluginDependenciesSpec.kapt(): PluginDependencySpec = kotlin("kapt")
fun PluginDependenciesSpec.junit5(): PluginDependencySpec = id("de.mannodermaus.android-junit5")