import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.DynamicFeaturePlugin
import com.android.build.gradle.internal.plugins.LibraryPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

subprojects {
    plugins.matching { it is AppPlugin || it is DynamicFeaturePlugin || it is LibraryPlugin }.whenPluginAdded {
        configure<com.android.build.gradle.BaseExtension> {
            compileSdkVersion(Apps.compileSdk)

            defaultConfig {
                minSdk = Apps.minSdkVersion
                targetSdk = Apps.targetSdkVersion
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }

            tasks.withType<KotlinCompile> {
                kotlinOptions {
                    jvmTarget = JavaVersion.VERSION_1_8.toString()
//                    allWarningsAsErrors = true
                }
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

