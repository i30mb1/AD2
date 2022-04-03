rootProject.name = "build-logic"

pluginManagement {
    includeBuild("../build-dependency")
}

plugins {
    id("convention.plugins")
    id("convention.dependencies")
}

include("bump-version-plugin")
include("extensions")
include("kotlin")
include("android")