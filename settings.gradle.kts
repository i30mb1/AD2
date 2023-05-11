rootProject.name = "AD2"

pluginManagement {
    includeBuild("build-logic/build-settings")
}

plugins {
    id("convention.settings")
    id("n7.plugins.settings")
}

include(
    ":app",
    ":micro-benchmark",
    ":macro-benchmark",
)

exec {
    commandLine("git", "config", "core.hooksPath", ".githooks")
}