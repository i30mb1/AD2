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
        const val provider = ":core:provider"
        const val spanParser = ":core:span-parser"
        const val appPreference = ":core:app-preference"
        const val mediaPlayer = ":core:media-player"
        const val retrofit = ":core:retrofit"
        const val common = ":core:common-jvm"
        const val updateManager = ":core:update-manager"
        const val logger = ":core:logger:app-logger"
        object YandexMetrics {
            const val api = ":core:logger:yandex:api"
            const val impl = ":core:logger:yandex:impl"
        }
    }

    object Feature {
        const val streams = ":feature:streams"
        const val heroesApp = ":feature:heroes:demo"
        const val heroesDomain = ":feature:heroes:domain"
        const val heroesUI = ":feature:heroes:ui"
        const val heroPage = ":feature:hero-page"
        const val itemPage = ":feature:item-page"
        const val settings = ":feature:settings"
        const val items = ":feature:items"
        const val drawer = ":feature:drawer"
        const val games = ":feature:games"
        const val tournaments = ":feature:tournaments"
        const val news = ":feature:news"
    }

}