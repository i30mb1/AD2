package n7.ad2.drawer.internal

import com.google.common.truth.Truth
import com.squareup.moshi.Moshi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import n7.ad2.PreferenceFake
import n7.ad2.ResourcesFake
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.drawer.internal.data.remote.SettingsApiFake
import n7.ad2.drawer.internal.domain.usecase.GetMenuItemsUseCase
import n7.ad2.drawer.internal.domain.vo.VOMenu
import n7.ad2.logger.LoggerFake
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class DrawerViewModelTest {

    @get:Rule val coroutineRule = CoroutineTestRule(StandardTestDispatcher())

    private lateinit var drawerViewModel: DrawerViewModel
    private val settingsApi = SettingsApiFake()

    @Before
    fun before() {
        drawerViewModel = DrawerViewModel(
            GetMenuItemsUseCase(
                ResourcesFake(),
                PreferenceFake(),
                settingsApi,
                LoggerFake(),
                Moshi.Builder().build(),
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
            )
    }

}