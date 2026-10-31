package n7.ad2.drawer.internal

import LoggerFake
import PreferenceFake
import ResourcesFake
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.drawer.internal.data.remote.model.VOMenuType
import n7.ad2.drawer.internal.domain.usecase.GetMenuItemsUseCase
import n7.ad2.feature.drawer.R
import org.junit.Rule
import org.junit.Test

internal class GetMenuItemsUseCaseTest {

    @get:Rule val coroutineRule = CoroutineTestRule(StandardTestDispatcher())

    private val settingsApi = SettingsApiFake()
    private val useCase = GetMenuItemsUseCase(ResourcesFake(), PreferenceFake(), settingsApi, LoggerFake(), coroutineRule.dispatchers)

    @Test
    fun `every section has its own subtitle`() = runTest {
        settingsApi.isError = true
        val subtitles = useCase().single().associate { it.type to it.subtitle }
        Truth.assertThat(subtitles).containsExactly(
            VOMenuType.HEROES, R.string.heroes_subtitle,
            VOMenuType.ITEMS, R.string.items_subtitle,
            VOMenuType.NEWS, R.string.news_subtitle,
            VOMenuType.TOURNAMENTS, R.string.tournaments_subtitle,
            VOMenuType.STREAMS, R.string.streams_subtitle,
            VOMenuType.GAMES, R.string.games_subtitle,
        )
    }

    @Test
    fun `disabled section says it is disabled`() = runTest {
        val news = useCase().single().first { it.type == VOMenuType.NEWS }
        Truth.assertThat(news.isEnable).isFalse()
        Truth.assertThat(news.subtitle).isEqualTo(R.string.menu_disabled)
    }
}
