enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "build-logic"

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral() // тут лежит kotlin-stdlib-jdk8
        maven { setUrl("https://jitpack.io") } // detekt
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal() // kotlin-dsl, kotlin, jvm, kapt, org.jetbrains.kotlin.android
        mavenCentral()
        google()
    }
}

include("bump-version-plugin")
include("measure-build-time")
include("build-extensions")
include("kotlin")
include("android")
include("detekt")
