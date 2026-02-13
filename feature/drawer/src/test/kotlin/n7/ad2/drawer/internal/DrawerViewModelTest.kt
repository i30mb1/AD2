package n7.ad2.drawer.internal

import LoggerFake
import PreferenceFake
import ResourcesFake
import com.google.common.truth.Truth
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.drawer.internal.data.remote.SettingsApi
import n7.ad2.drawer.internal.data.remote.model.Menu
import n7.ad2.drawer.internal.data.remote.model.Settings
import n7.ad2.drawer.internal.data.remote.model.VOMenuType
import n7.ad2.drawer.internal.domain.usecase.GetMenuItemsUseCase
import n7.ad2.drawer.internal.domain.vo.VOMenu
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class DrawerViewModelTest {

    @get:Rule val coroutineRule = CoroutineTestRule(StandardTestDispatcher())

    private lateinit var drawerViewModel: DrawerViewModel
    private val settingsApi = SettingsApiFake()

    @Before
    fun before() {
        // R.drawable.img - removed reference to testFixtures R class
        drawerViewModel = DrawerViewModel(
            GetMenuItemsUseCase(
                ResourcesFake(),
                PreferenceFake(),
                settingsApi,
                LoggerFake(),
                coroutineRule.dispatchers,
            ),
            PreferenceFake(),
        )
    }

    @Test
    fun `when fail return default items`() = runTest {
        settingsApi.isError = true
        advanceUntilIdle()
        val values = drawerViewModel.menu.value
        val actualItems = values.map(VOMenu::javaClass)
        Truth.assertThat(actualItems)
            .containsExactly(
                VOMenu::class.java,
                VOMenu::class.java,
                VOMenu::class.java,
                VOMenu::class.java,
                VOMenu::class.java,
                VOMenu::class.java,
            )
    }

    @Test
    fun `when success return items`() = runTest {
        advanceUntilIdle()
        val values = drawerViewModel.menu.value
        val actualItems = values.map(VOMenu::javaClass)
        Truth.assertThat(actualItems)
            .containsExactly(
                VOMenu::class.java,
                VOMenu::class.java,
                VOMenu::class.java,
                VOMenu::class.java,
                VOMenu::class.java,
                VOMenu::class.java,
            )
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
