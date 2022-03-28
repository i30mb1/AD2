rootProject.name = "build-logic-settings"

include("bump-version-plugin")

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