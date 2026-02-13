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
        fun empty(): Navigator = object : Navigator {
            override val settingsApi: SettingsApi
                get() = error("empty realization for demo app's")
            override val streamApi: StreamsApi
                get() = error("empty realization for demo app's")
            override val heroesApi: HeroesApi
                get() = error("empty realization for demo app's")
            override val newsApi: NewsApi
                get() = error("empty realization for demo app's")
            override val itemsApi: ItemsApi
                get() = error("empty realization for demo app's")
            override val gamesApi: GamesApi
                get() = error("empty realization for demo app's")
            override val tournamentsApi: TournamentsApi
                get() = error("empty realization for demo app's")
            override val drawerApi: DrawerApi
                get() = error("empty realization for demo app's")
            override val heroPageApi: HeroPageApi
                get() = error("empty realization for demo app's")
            override val itemPageApi: ItemPageApi
                get() = error("empty realization for demo app's")
        }
    }
}
