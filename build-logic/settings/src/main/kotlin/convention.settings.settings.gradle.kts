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
        google()  // com.android.library, com.android.tools.build:gradle
        maven { setUrl("https://jitpack.io") } // detekt
    }
    plugins {
        kotlin("kapt") version "1.7.20"
    }
}
includeBuild("build-logic")

val folders = listOf("core", "feature")
for (folder in folders) {
    File(folder).listFiles()?.forEach { project ->
        include(":${folder}:${project.name}")
    }
}