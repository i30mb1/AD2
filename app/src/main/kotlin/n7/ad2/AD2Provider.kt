package n7.ad2

import ad2.n7.news.api.NewsProvider
import ad2.n7.settings.api.SettingsProvider
import n7.ad2.drawer.api.DrawerProvider
import n7.ad2.games.api.GamesProvider
import n7.ad2.hero_page.api.HeroPageProvider
import n7.ad2.heroes.api.HeroesProvider
import n7.ad2.item_page.api.ItemPageProvider
import n7.ad2.items.api.ItemsProvider
import n7.ad2.provider.Provider
import n7.ad2.provider.api.DrawerApi
import n7.ad2.provider.api.GamesApi
import n7.ad2.provider.api.HeroPageApi
import n7.ad2.provider.api.HeroesApi
import n7.ad2.provider.api.ItemPageApi
import n7.ad2.provider.api.ItemsApi
import n7.ad2.provider.api.NewsApi
import n7.ad2.provider.api.SettingsApi
import n7.ad2.provider.api.StreamsApi
import n7.ad2.streams.api.StreamsProvider
import n7.ad2.tournaments.api.TournamentsProvider

object AD2Provider : Provider {
    override val streamApi: StreamsApi = StreamsProvider()
    override val heroesApi: HeroesApi = HeroesProvider()
    override val itemsApi: ItemsApi = ItemsProvider()
    override val gamesApi: GamesApi = GamesProvider()
    override val tournamentsApi = TournamentsProvider()
    override val newsApi: NewsApi = NewsProvider()
    override val drawerApi: DrawerApi = DrawerProvider()
    override val heroPageApi: HeroPageApi = HeroPageProvider()
    override val itemPageApi: ItemPageApi = ItemPageProvider()
    override val settingsApi: SettingsApi = SettingsProvider()
}