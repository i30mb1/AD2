rootProject.name = "AD2"

pluginManagement {
    includeBuild("build-logic/settings")
}

plugins {
    id("convention.settings")
}

include(
    ":app",
    ":micro-benchmark",
    ":macro-benchmark",
)
include(
    ":core:dagger",
    ":core:common-android",
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
    ":core:retrofit",
    ":core:common-jvm",
    ":core:update-manager",
    ":core:yandex-metrics",
)
include(
    ":feature:streams",
    ":feature:drawer",
    ":feature:heroes",
    ":feature:items",
    ":feature:games",
    ":feature:tournaments",
    ":feature:news",
    ":feature:settings",
    ":feature:hero-page",
    ":feature:item-page",
)