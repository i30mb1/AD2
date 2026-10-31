package n7.ad2.drawer.internal

import LoggerFake
import PreferenceFake
import ResourcesFake
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.drawer.internal.data.remote.SettingsApi
import n7.ad2.drawer.internal.data.remote.model.Menu
import n7.ad2.drawer.internal.data.remote.model.Settings
import n7.ad2.drawer.internal.data.remote.model.VOMenuType
import n7.ad2.drawer.internal.domain.usecase.GetMenuItemsUseCase
import n7.ad2.drawer.internal.domain.usecase.GetMenuInfoUseCase
import n7.ad2.drawer.internal.domain.vo.VOMenu
import n7.ad2.drawer.internal.domain.vo.VOMenuInfo
import n7.ad2.heroes.domain.model.Hero
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import n7.ad2.items.domain.model.Item
import n7.ad2.items.domain.model.News
import n7.ad2.items.domain.usecase.GetItemsUseCase
import n7.ad2.items.domain.usecase.GetNewsUseCase
import org.junit.Rule
import org.junit.Test

internal class DrawerViewModelTest {

    @get:Rule val coroutineRule = CoroutineTestRule(StandardTestDispatcher())

    private val settingsApi = SettingsApiFake()

    private val infoSource = MenuInfoSourceFake()
    private val getMenuInfoUseCase = GetMenuInfoUseCase(
        getHeroesUseCase = object : GetHeroesUseCase {
            override fun invoke(): Flow<List<Hero>> = flowOf(emptyList())
        },
        getItemsUseCase = object : GetItemsUseCase {
            override fun invoke(): Flow<List<Item>> = flowOf(emptyList())
        },
        getNewsUseCase = object : GetNewsUseCase {
            override suspend fun invoke(page: Int): Flow<List<News>> = flowOf(listOf(News("", "", page, "", 500L, "", "", false)))
        },
        source = infoSource,
        logger = LoggerFake(),
        dispatchers = coroutineRule.dispatchers,
    )

    private fun createViewModel() = DrawerViewModel(
        GetMenuItemsUseCase(
            ResourcesFake(),
            PreferenceFake(),
            settingsApi,
            LoggerFake(),
            coroutineRule.dispatchers,
        ),
        getMenuInfoUseCase,
        infoSource,
        PreferenceFake(),
    )

    @Test
    fun `when fail return default items`() = runTest {
        settingsApi.isError = true
        val drawerViewModel = createViewModel()
        advanceUntilIdle()
        Truth.assertThat(drawerViewModel.menu.value.map(VOMenu::type))
            .containsExactly(VOMenuType.HEROES, VOMenuType.ITEMS, VOMenuType.NEWS, VOMenuType.TOURNAMENTS, VOMenuType.STREAMS, VOMenuType.GAMES)
            .inOrder()
    }

    @Test
    fun `when success return items`() = runTest {
        val drawerViewModel = createViewModel()
        advanceUntilIdle()
        Truth.assertThat(drawerViewModel.menu.value.map(VOMenu::type))
            .containsExactly(VOMenuType.HEROES, VOMenuType.NEWS)
            .inOrder()
    }

    @Test
    fun `menu cards get the live numbers`() = runTest {
        settingsApi.isError = true
        infoSource.newsSeenAt = 100L
        val drawerViewModel = createViewModel()
        advanceUntilIdle()
        val info = drawerViewModel.menu.value.associate { it.type to it.info }
        Truth.assertThat(info[VOMenuType.NEWS]).isEqualTo(VOMenuInfo.News(1))
        Truth.assertThat(info[VOMenuType.TOURNAMENTS]).isEqualTo(VOMenuInfo.LiveMatches(2))
    }

    @Test
    fun `opening the news marks them read`() = runTest {
        settingsApi.isError = true
        infoSource.newsSeenAt = 100L
        val drawerViewModel = createViewModel()
        advanceUntilIdle()
        drawerViewModel.updateMenu(drawerViewModel.menu.value.first { it.type == VOMenuType.NEWS })
        advanceUntilIdle()
        Truth.assertThat(drawerViewModel.menu.value.first { it.type == VOMenuType.NEWS }.info).isNull()
        Truth.assertThat(infoSource.newsSeenAt!!).isGreaterThan(500L)
    }
}

internal class SettingsApiFake : SettingsApi {
    var isError = false
    override suspend fun getSettings(): Settings = if (isError) {
        error("oops!")
    } else {
        Settings(
            menu = listOf(
                Menu(VOMenuType.HEROES, true),
                Menu(VOMenuType.NEWS, false),
            ),
        )
    }
}
