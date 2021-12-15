import org.gradle.kotlin.dsl.kotlin
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

fun PluginDependenciesSpec.dynamicFeature(): PluginDependencySpec = id("com.android.dynamic-feature")

fun PluginDependenciesSpec.androidApp(): PluginDependencySpec = id("android-application-convention")

fun PluginDependenciesSpec.safeargs(): PluginDependencySpec = id("androidx.navigation.safeargs.kotlin")

fun PluginDependenciesSpec.junit5(): PluginDependencySpec = id("de.mannodermaus.android-junit5")

fun PluginDependenciesSpec.kotlinAndroid(): PluginDependencySpec = kotlin("android")

fun PluginDependenciesSpec.kotlinAndroidExt(): PluginDependencySpec = kotlin("android.extensions") // kotlin synthetics

fun PluginDependenciesSpec.kotlinKapt(): PluginDependencySpec = kotlin("kapt")

fun PluginDependenciesSpec.googleServices(): PluginDependencySpec = id("com.google.gms.google-services")

fun PluginDependenciesSpec.javaLibrary(): PluginDependencySpec = id("java-library")

fun PluginDependenciesSpec.androidLibrary(): PluginDependencySpec = id("android-library-convention")

fun PluginDependenciesSpec.kotlin(): PluginDependencySpec = id("kotlin-jvm-convention")