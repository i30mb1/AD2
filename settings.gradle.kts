enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "AD2"

pluginManagement {
    includeBuild("build-logic/build-settings")
}

plugins {
    id("n7.plugins.settings")
}

include(":feature:games:xo:cli-test")
