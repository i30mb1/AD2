rootProject.name = "build-logic"

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()  // тут лежит kotlin-stdlib-jdk8
        maven { setUrl("https://jitpack.io") } // detekt
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal() // kotlin-dsl, kotlin, jvm, kapt, org.jetbrains.kotlin.android
    }
}

include("bump-version-plugin")
include("measure-build-time")
include("extensions")
include("kotlin")
include("android")
includeBuild("settings")