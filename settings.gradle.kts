rootProject.name = "AD2"

pluginManagement {
    includeBuild("build-logic/settings")
}

plugins {
    id("convention.settings")
}

include(
    ":app",
    ":micro-benchmark",
    ":macro-benchmark",
)