rootProject.name = "build-logic"

pluginManagement {
    includeBuild("dependencies")
    repositories {
        gradlePluginPortal() // kotlin-dsl, kotlin, jvm, kapt, org.jetbrains.kotlin.android
    }
}

plugins {
    id("convention.dependency")
}

include("bump-version-plugin")
include("measure-build-time")
include("extensions")
include("kotlin")
include("android")
include("detekt")