rootProject.name = "build-logic-settings"

include("bump-version-plugin")
include("extensions")

dependencyResolutionManagement {
    repositories {
        exclusiveContent {
            forRepository { mavenCentral() }
            filter {
                includeGroupByRegex("org\\.jetbrains.*")
            }
        }
    }
}