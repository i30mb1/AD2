include(":app")
include(":feature:streams")
include(":core:rules")
include(":core:parser")
include(":core:android")
include(":core:dagger")
include(":core:logger")
include(":core:coroutines")
include(":core:database")
include(":core:repositories")
include(":feature:drawer")
include(":core:ui")
include(":core:provider")
include(":feature:heroes")
include(":feature:items")
include(":feature:games")
include(":feature:tournaments")
include(":feature:news")
include(":core:app-preference")
include(":core:span-parser")
include(":feature:hero-page")
include(":feature:item-page")
include(":core:ktx")
include(":core:media-player")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

enableFeaturePreview("VERSION_CATALOGS")