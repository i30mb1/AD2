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
}
