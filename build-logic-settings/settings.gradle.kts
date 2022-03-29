rootProject.name = "build-logic-settings"

include("bump-version-plugin")
include("extensions")
include("dependency")
include("kotlin")
include("android")

dependencyResolutionManagement {
    repositories {
        exclusiveContent {
            forRepository { mavenCentral() }
            filter {
                includeGroupByRegex("org\\.jetbrains.*")
                includeGroupByRegex("com\\.google.*")
                includeModule("de.undercouch", "gradle-download-task")
                includeModule("com.github.gundy", "semver4j")
                includeModule("org.checkerframework", "checker-qual")
            }
        }
        exclusiveContent {
            forRepository { google() }
            filter {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google\\.android\\..*")
                includeGroupByRegex("androidx\\..*")
                includeGroup("com.google.testing.platform")
            }
        }
    }
}