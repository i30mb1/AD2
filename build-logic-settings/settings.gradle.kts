rootProject.name = "build-logic-settings"

pluginManagement {
    includeBuild("../build-dependency")
}

plugins {
    id("convention.plugins")
    id("convention.dependencies")
}

include("bump-version-plugin")
include("extensions")
include("dependency")
include("kotlin")
include("android")