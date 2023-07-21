object Module {

    object Core {
        const val android = ":core:common-android"
        const val rules = ":core:rules"
        const val dagger = ":core:dagger"

        const val coroutines = ":core:coroutines"
        const val ktx = ":core:ktx"
        const val database = ":core:database"
        const val repositories = ":core:repositories"
        const val ui = ":core:ui"
        const val navigator = ":core:navigator"
        const val spanParser = ":core:span-parser"
        const val appPreference = ":core:app-preference"
        const val mediaPlayer = ":core:media-player"
        const val retrofit = ":core:retrofit"
        const val common = ":core:common-jvm"
        const val updateManager = ":core:update-manager"
        const val logger = ":core:logger:app-logger"
        const val yandexMetrics = ":core:logger:yandex"
        const val commonApplication = ":core:common-application"
    }

    object Feature {
        const val streams = ":feature:streams"

        object Heroes {
            const val api = ":feature:heroes:domain:api"
            const val impl = ":feature:heroes:domain:impl"
            const val wiring = ":feature:heroes:domain:wiring"
            const val demo = ":feature:heroes:demo"
            const val ui = ":feature:heroes:ui"
        }

        object HeroPage {
            const val api = ":feature:hero-page:domain:api"
            const val impl = ":feature:hero-page:domain:impl"
            const val demo = ":feature:hero-page:demo"
            const val ui = ":feature:hero-page:ui"
        }

        const val itemPage = ":feature:item-page"
        const val settings = ":feature:settings"
        const val items = ":feature:items"
        const val drawer = ":feature:drawer"
        const val games = ":feature:games"
        const val tournaments = ":feature:tournaments"
        const val news = ":feature:news"
    }

}
