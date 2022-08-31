package n7.ad2.drawer.internal

import LoggerFake
import ResourcesFake
import com.google.common.truth.Truth
import com.squareup.moshi.Moshi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import n7.ad2.app_preference.Preference
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.drawer.internal.data.remote.SettingsApiFake
import n7.ad2.drawer.internal.domain.usecase.GetMenuItemsUseCase
import n7.ad2.drawer.internal.domain.vo.VOMenu
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// https://issuetracker.google.com/issues/219687418
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

class PreferenceFake : Preference {
    override suspend fun isNeedToUpdateSettings(): Boolean = true
    override suspend fun saveSettings(data: String) = Unit
    override suspend fun getSettings(): String = ""
    override suspend fun saveDate(date: Int) = Unit
    override suspend fun getDate(): Int = 0
    override suspend fun setFingerCoordinateEnabled(isEnabled: Boolean) = Unit
    override suspend fun isFingerCoordinateEnabled(): Boolean = true
    override suspend fun isLogWidgetEnabled(): Boolean = true
}