package n7.ad2.drawer.internal

import com.google.common.truth.Truth
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import n7.ad2.PreferenceFake
import n7.ad2.ResourcesFake
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.drawer.internal.data.remote.SettingsApiFake
import n7.ad2.drawer.internal.domain.usecase.GetMenuItemsUseCase
import n7.ad2.drawer.internal.domain.vo.VOMenu
import n7.ad2.logger.LoggerFake
import org.junit.Rule
import org.junit.Test

internal class DrawerViewModelTest {

    @get:Rule val coroutineRule = CoroutineTestRule()

    private val drawerViewModel = DrawerViewModel(
        GetMenuItemsUseCase(
            ResourcesFake(),
            PreferenceFake(),
            SettingsApiFake(),
            LoggerFake(),
            Moshi.Builder().build(),
            coroutineRule.dispatchers
        ),
        PreferenceFake(),
    )

    @Test
    fun hehe() = runTest {
        val values = mutableListOf<List<VOMenu>>()
        val flow = launch(UnconfinedTestDispatcher()) {
            drawerViewModel.menu.toList(values)
        }
        val actualItems = values.last().map { it.javaClass }
        Truth.assertThat(actualItems).containsExactly(
            VOMenu::class.java
        )
        flow.cancel()
    }

}