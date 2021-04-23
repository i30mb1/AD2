include(":rules")
include(":app")
include(":parseInfo")
include(":feature_streams")

dependencyResolutionManagement {
    repositories {
        jcenter()
        google()
        maven("https://maven.google.com")
    }
}