package n7.ad2

import n7.ad2.drawer.api.DrawerProvider
import n7.ad2.games.api.GamesProvider
import n7.ad2.hero.page.api.HeroPageProvider
import n7.ad2.heroes.ui.api.HeroesProvider
import n7.ad2.itempage.api.ItemPageProvider
import n7.ad2.items.api.ItemsProvider
import n7.ad2.navigator.Navigator
import n7.ad2.navigator.api.DrawerApi
import n7.ad2.navigator.api.GamesApi
import n7.ad2.navigator.api.HeroPageApi
import n7.ad2.navigator.api.HeroesApi
import n7.ad2.navigator.api.ItemPageApi
import n7.ad2.navigator.api.ItemsApi
import n7.ad2.navigator.api.NewsApi
import n7.ad2.navigator.api.SettingsApi
import n7.ad2.navigator.api.StreamsApi
import n7.ad2.news.ui.api.NewsProvider
import n7.ad2.settings.api.SettingsProvider
import n7.ad2.streams.api.StreamsProvider
import n7.ad2.tournaments.api.TournamentsProvider

object AD2Navigator : Navigator {
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
