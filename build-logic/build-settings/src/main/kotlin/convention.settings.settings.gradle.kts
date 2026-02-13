dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral() // тут лежит kotlin-stdlib-jdk8
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
        google() // com.android.library, com.android.tools.build:gradle
        maven { setUrl("https://jitpack.io") } // detekt
        gradlePluginPortal() // kotlin-dsl, kotlin, jvm, kapt, org.jetbrains.kotlin.android
    }
}
includeBuild("build-logic")
