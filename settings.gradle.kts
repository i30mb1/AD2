enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "AD2"

pluginManagement {
    includeBuild("build-logic/build-settings")
}

plugins {
    id("n7.plugins.settings")
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// include(":feature:games:xo:cli-test")
