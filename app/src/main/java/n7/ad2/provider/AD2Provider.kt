package n7.ad2.provider

import ad2.n7.news.api.NewsProvider
import n7.ad2.android.extension.lazyUnsafe
import n7.ad2.drawer.api.DrawerProvider
import n7.ad2.games.api.GamesProvider
import n7.ad2.hero_page.api.HeroPageProvider
import n7.ad2.heroes.api.HeroesProvider
import n7.ad2.item_page.api.ItemPageProvider
import n7.ad2.items.api.ItemsProvider
import n7.ad2.provider.api.DrawerApi
import n7.ad2.provider.api.GamesApi
import n7.ad2.provider.api.HeroPageApi
import n7.ad2.provider.api.HeroesApi
import n7.ad2.provider.api.ItemPageApi
import n7.ad2.provider.api.ItemsApi
import n7.ad2.provider.api.NewsApi
import n7.ad2.provider.api.StreamsApi
import n7.ad2.streams.api.StreamsProvider
import n7.ad2.tournaments.api.TournamentsProvider

object AD2Provider : Provider {

    override val streamApi: StreamsApi by lazyUnsafe { StreamsProvider() }
    override val heroesApi: HeroesApi by lazyUnsafe { HeroesProvider() }
    override val itemsApi: ItemsApi by lazyUnsafe { ItemsProvider() }
    override val gamesApi: GamesApi by lazyUnsafe { GamesProvider() }
    override val tournamentsApi by lazyUnsafe { TournamentsProvider() }
    override val newsApi: NewsApi by lazyUnsafe { NewsProvider() }
    override val drawerApi: DrawerApi by lazyUnsafe { DrawerProvider() }
    override val heroPageApi: HeroPageApi by lazyUnsafe { HeroPageProvider() }
    override val itemPageApi: ItemPageApi by lazyUnsafe { ItemPageProvider() }
}