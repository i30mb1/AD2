package n7.ad2.provider

import n7.ad2.provider.api.DrawerApi
import n7.ad2.provider.api.GamesApi
import n7.ad2.provider.api.HeroPageApi
import n7.ad2.provider.api.HeroesApi
import n7.ad2.provider.api.ItemsApi
import n7.ad2.provider.api.NewsApi
import n7.ad2.provider.api.StreamsApi
import n7.ad2.provider.api.TournamentsApi

interface Provider {

    val streamApi: StreamsApi
    val heroesApi: HeroesApi
    val newsApi: NewsApi
    val itemsApi: ItemsApi
    val gamesApi: GamesApi
    val tournamentsApi: TournamentsApi
    val drawerApi: DrawerApi
    val heroPageApi: HeroPageApi

}