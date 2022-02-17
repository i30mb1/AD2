dependencyResolutionManagement { // репозитории для все проектов (модулей)
    repositories {
        mavenCentral()
        exclusiveContent {
            forRepository { google() }
            filter {
                includeGroupByRegex("androidx\\..+")
                includeGroupByRegex("com.android.*")
                includeGroupByRegex("com.google.android.+")
            }
        }
    }
}

pluginManagement { // блок выполянется до конфигурации проекта
    repositories {
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin { // хитрый способ динамический подключить плагины если они начнут использоваться в проекте
            val pluginId = requested.id.id
            if (requested.id.namespace == "org.jetbrains.kotlin") useVersion("1.5.30")
            if (requested.id.namespace == "com.android") useModule("com.android.tools.build:gradle:7.0.4")
            if (pluginId == "com.gradle.enterprise") useVersion("3.7.2")
        }
    }
}

include(":app")
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