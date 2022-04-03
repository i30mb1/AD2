pluginManagement {
    repositories {
        exclusiveContent {
            forRepository {
                gradlePluginPortal() // kotlin-dsl, kotlin, jvm, kapt
            }
            filter {
                includeGroup("org.gradle.kotlin")
            }
        }

    }

    resolutionStrategy {
        eachPlugin {
            val pluginId = requested.id.id
            val namespace = requested.id.namespace
            when {
                namespace == "org.gradle.kotlin" -> useVersion("2.1.7")
                namespace == "org.jetbrains.kotlin" -> useVersion("1.6.10") // useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.30")
                namespace == "com.android" -> useVersion("7.1.2") // useModule("com.android.tools.build:gradle:7.1.2")
                pluginId == "androidx.benchmark" -> useModule("androidx.benchmark:benchmark-gradle-plugin:1.1.0-beta05")
            }
        }
    }

}