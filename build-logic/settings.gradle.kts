rootProject.name = "build-logic"

pluginManagement {
    includeBuild("dependencies")
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