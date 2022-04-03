pluginManagement {
    repositories { maven { setUrl("https://plugins.gradle.org/m2/") } }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "org.gradle.kotlin") useVersion("2.1.7")
            else if (requested.id.namespace == "org.jetbrains.kotlin") useVersion("1.6.10")
        }
    }
}