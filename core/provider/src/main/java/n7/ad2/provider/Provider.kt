package n7.ad2.provider

import n7.ad2.provider.api.HeroesApi
import n7.ad2.provider.api.ItemsApi
import n7.ad2.provider.api.StreamsApi

interface Provider {

    val streamApi: StreamsApi
    val heroesApi: HeroesApi
    val itemsApi: ItemsApi

}