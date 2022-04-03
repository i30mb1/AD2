pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin { // хитрый способ динамический подключить плагины если они начнут использоваться в проекте
            val pluginId = requested.id.id
            if (requested.id.namespace == "org.jetbrains.kotlin") useVersion("1.6.30") // useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.30")
            if (requested.id.namespace == "com.android") useVersion("7.1.2") // useModule("com.android.tools.build:gradle:7.1.2")
            if (pluginId == "androidx.benchmark") useModule("androidx.benchmark:benchmark-gradle-plugin:1.1.0-beta05")
            if (pluginId == "com.gradle.enterprise") useVersion("3.7.2")
        }
    }
}

dependencyResolutionManagement { // репозитории для все проектов (модулей)
    repositories {
        google()
        mavenCentral()
        exclusiveContent {
            forRepository { google() }
            filter {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google\\.android\\..*")
            }
        }
    }
}