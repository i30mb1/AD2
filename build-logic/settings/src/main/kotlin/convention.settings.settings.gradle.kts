dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()  // тут лежит kotlin-stdlib-jdk8
    }
    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.versions.toml"))
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

//rootDir.listFiles()
//    ?.filter { File(it, "build.gradle.kts").exists() }
//    ?.forEach { include(it.name) }
//
//rootDir.listFiles()
//    ?.filter { it.name in listOf("core", "feature") }
//    ?.flatMap { folder ->
//        folder.listFiles().mapNotNull { subFolder ->
//            if (File("${folder.name}/${subFolder.name}","build.gradle.kts").exists())
//            ":${folder.name}:${subFolder.name}" else null
//        }
//    }
//    ?.forEach { include(it) }