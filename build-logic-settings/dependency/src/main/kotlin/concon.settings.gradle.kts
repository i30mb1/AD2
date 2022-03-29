pluginManagement {
    repositories {
        mavenCentral()
        google()
    }
    resolutionStrategy {
        eachPlugin { // хитрый способ динамический подключить плагины если они начнут использоваться в проекте
            val pluginId = requested.id.id
//            if (requested.id.namespace == "org.jetbrains.kotlin") useVersion("1.5.30") // useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.30")
//            if (requested.id.namespace == "com.android") useVersion("7.0.4") // useModule("com.android.tools.build:gradle:7.1.2")
            if (pluginId == "androidx.benchmark") useModule("androidx.benchmark:benchmark-gradle-plugin:1.0.0")
            if (pluginId == "com.gradle.enterprise") useVersion("3.7.2")
        }
    }
}

dependencyResolutionManagement { // репозитории для все проектов (модулей)
    repositories {
        mavenCentral()
        exclusiveContent {
            forRepository { google() }
            filter {
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com.android.*")
                includeGroupByRegex("com.google.android.*")
            }
        }
    }
}