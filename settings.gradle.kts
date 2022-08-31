rootProject.name = "AD2"

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()  // тут лежит kotlin-stdlib-jdk8
    }
    versionCatalogs {
        create("libs") {
            from(files("build-logic/gradle/libs.versions.toml"))
        }
    }
}

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal() // kotlin-dsl, kotlin, jvm, kapt, org.jetbrains.kotlin.android
        google()  // com.android.library
    }

    resolutionStrategy {
        eachPlugin {
            val pluginId = requested.id.id
            val namespace = requested.id.namespace
            when {
                namespace == "org.gradle.kotlin" -> useVersion("2.1.7")
                namespace == "org.jetbrains.kotlin" -> useVersion("1.6.10") // useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.30")
                namespace == "com.android" -> useVersion("7.1.2") // useModule("com.android.tools.build:gradle:7.1.2")
                pluginId == "androidx.benchmark" -> useModule("androidx.benchmark:benchmark-gradle-plugin:1.1.0-beta05")
            }
        }
    }

}

include(":app")
include(":micro-benchmark")
include(
    ":core:dagger",
    ":core:android",
    ":core:parser",
    ":core:rules",
    ":core:logger",
    ":core:coroutines",
    ":core:database",
    ":core:repositories",
    ":core:ui",
    ":core:provider",
    ":core:app-preference",
    ":core:span-parser",
    ":core:ktx",
    ":core:media-player",
    ":core:retrofit",
    ":core:common",
    ":core:update-manager",
)
include(
    ":feature:streams",
    ":feature:drawer",
    ":feature:heroes",
    ":feature:items",
    ":feature:games",
    ":feature:tournaments",
    ":feature:news",
    ":feature:hero-page",
    ":feature:item-page",
)