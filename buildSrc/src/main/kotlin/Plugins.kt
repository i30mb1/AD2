import org.gradle.kotlin.dsl.kotlin
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

fun PluginDependenciesSpec.dynamicFeature(): PluginDependencySpec = id("com.android.dynamic-feature")
fun PluginDependenciesSpec.application(): PluginDependencySpec = id("android-application-convention")
fun PluginDependenciesSpec.androidLibrary(): PluginDependencySpec = id("android-library-convention")
fun PluginDependenciesSpec.benchmark(): PluginDependencySpec = id("androidx.benchmark")
fun PluginDependenciesSpec.kotlinLibrary(): PluginDependencySpec = id("kotlin-jvm-convention")
fun PluginDependenciesSpec.kapt(): PluginDependencySpec = kotlin("kapt")
fun PluginDependenciesSpec.junit5(): PluginDependencySpec = id("de.mannodermaus.android-junit5")