rootProject.name = "build-logic-settings"

dependencyResolutionManagement { // репозитории для все проектов (модулей)
    repositories {
        mavenCentral()
    }
}

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "org.jetbrains.kotlin") useVersion("1.5.30")
        }
    }
}

include("bump-version-plugin")
include("extensions")
include("dependency")
include("kotlin")
include("android")