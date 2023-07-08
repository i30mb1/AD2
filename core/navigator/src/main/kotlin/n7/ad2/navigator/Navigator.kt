package n7.ad2.navigator

import n7.ad2.navigator.api.DrawerApi
import n7.ad2.navigator.api.GamesApi
import n7.ad2.navigator.api.HeroPageApi
import n7.ad2.navigator.api.HeroesApi
import n7.ad2.navigator.api.ItemPageApi
import n7.ad2.navigator.api.ItemsApi
import n7.ad2.navigator.api.NewsApi
import n7.ad2.navigator.api.SettingsApi
import n7.ad2.navigator.api.StreamsApi
import n7.ad2.navigator.api.TournamentsApi

interface Navigator {
    val settingsApi: SettingsApi
    val streamApi: StreamsApi
    val heroesApi: HeroesApi
    val newsApi: NewsApi
    val itemsApi: ItemsApi
    val gamesApi: GamesApi
    val tournamentsApi: TournamentsApi
    val drawerApi: DrawerApi
    val heroPageApi: HeroPageApi
    val itemPageApi: ItemPageApi

    companion object {
        fun empty(): Navigator {
            return object : Navigator {
                override val settingsApi: SettingsApi
                    get() = TODO("Not yet implemented")
                override val streamApi: StreamsApi
                    get() = TODO("Not yet implemented")
                override val heroesApi: HeroesApi
                    get() = TODO("Not yet implemented")
                override val newsApi: NewsApi
                    get() = TODO("Not yet implemented")
                override val itemsApi: ItemsApi
                    get() = TODO("Not yet implemented")
                override val gamesApi: GamesApi
                    get() = TODO("Not yet implemented")
                override val tournamentsApi: TournamentsApi
                    get() = TODO("Not yet implemented")
                override val drawerApi: DrawerApi
                    get() = TODO("Not yet implemented")
                override val heroPageApi: HeroPageApi
                    get() = TODO("Not yet implemented")
                override val itemPageApi: ItemPageApi
                    get() = TODO("Not yet implemented")
            }
        }
    }
}
