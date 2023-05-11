rootProject.name = "AD2"

pluginManagement {
    includeBuild("build-logic/build-settings")
}

plugins {
    id("n7.plugins.settings")
}

include(
    ":app",
    ":micro-benchmark",
    ":macro-benchmark",
)