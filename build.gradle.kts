import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.DynamicFeaturePlugin
import com.android.build.gradle.internal.plugins.LibraryPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

allprojects {
    repositories {
        jcenter()
        google()
        maven("https://maven.google.com")
    }
}

subprojects {
    plugins.matching { it is AppPlugin || it is DynamicFeaturePlugin || it is LibraryPlugin }.whenPluginAdded {
        configure<com.android.build.gradle.BaseExtension> {
            compileSdkVersion(Apps.compileSdk)

            defaultConfig {
                minSdkVersion(Apps.minSdk)
                targetSdkVersion(Apps.targetSdk)
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }

            tasks.withType<KotlinCompile> {
                kotlinOptions {
                    noStdlib = true
                    jvmTarget = JavaVersion.VERSION_1_8.toString()
                    freeCompilerArgs = listOf("-Xallow-result-return-type")
                }
            }
        }
    }
}

buildscript {
    repositories {
        jcenter()
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.0.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

