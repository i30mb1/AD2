enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        google()
    }
    resolutionStrategy {
        eachPlugin {
            val pluginId = requested.id.id
            if (pluginId.startsWith("org.jetbrains.kotlin")) useVersion("1.5.30")
            if (pluginId.startsWith("com.android")) useModule("com.android.tools.build:gradle:7.0.4")
        }
    }
}

include(":app")
include(
    ":core:dagger",
    ":core:android",
    ":core:parser",
    ":core:rules",
    ":core:logger",
    ":core:coroutines",
    ":core:database",
    ":core:repositories",
    ":core:ui",
    ":core:provider",
    ":core:app-preference",
    ":core:span-parser",
    ":core:ktx",
    ":core:media-player",
)
include(
    ":feature:streams",
    ":feature:drawer",
    ":feature:heroes",
    ":feature:items",
    ":feature:games",
    ":feature:tournaments",
    ":feature:news",
    ":feature:hero-page",
    ":feature:item-page",
)