rootProject.name = "build-logic-settings"

include("bump-version-plugin")
include("extensions")
include("dependency")

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