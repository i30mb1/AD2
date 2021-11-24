import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.DynamicFeaturePlugin
import com.android.build.gradle.internal.plugins.LibraryPlugin

subprojects {
    plugins.matching { it is AppPlugin || it is DynamicFeaturePlugin || it is LibraryPlugin }.whenPluginAdded {
        configure<com.android.build.gradle.BaseExtension> {
            compileSdkVersion(Apps.compileSdk)

            defaultConfig {
                minSdk = Apps.minSdkVersion
                targetSdk = Apps.targetSdkVersion
            }
        }
    }
}

buildscript {
    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.gradlePlugin}")
        classpath(kotlin("gradle-plugin", version = Versions.kotlin))
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

