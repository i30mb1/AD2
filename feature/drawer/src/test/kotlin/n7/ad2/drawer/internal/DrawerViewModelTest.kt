package n7.ad2.drawer.internal

import kotlinx.coroutines.test.StandardTestDispatcher
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.drawer.internal.data.remote.SettingsApiFake
import org.junit.Rule

// https://issuetracker.google.com/issues/219687418
internal class DrawerViewModelTest {

    @get:Rule val coroutineRule = CoroutineTestRule(StandardTestDispatcher())

    private lateinit var drawerViewModel: DrawerViewModel
    private val settingsApi = SettingsApiFake()

//    @Before
//    fun before() {
//        drawerViewModel = DrawerViewModel(
//            GetMenuItemsUseCase(
//                ResourcesFake(),
//                PreferenceFake(),
//                settingsApi,
//                LoggerFake(),
//                Moshi.Builder().build(),
//                coroutineRule.dispatchers,
//            ),
//            PreferenceFake(),
//        )
//    }

//    @Test
//    fun `when fail return default items`() = runTest {
//        settingsApi.isError = true
//        advanceUntilIdle()
//        val values = drawerViewModel.menu.value
//        val actualItems = values.map(VOMenu::javaClass)
//        Truth.assertThat(actualItems)
//            .containsExactly(
//                VOMenu::class.java,
//                VOMenu::class.java,
//                VOMenu::class.java,
//                VOMenu::class.java,
//                VOMenu::class.java,
//                VOMenu::class.java,
//            )
//    }
//
//    @Test
//    fun `when success return items`() = runTest {
//        advanceUntilIdle()
//        val values = drawerViewModel.menu.value
//        val actualItems = values.map(VOMenu::javaClass)
//        Truth.assertThat(actualItems)
//            .containsExactly(
//                VOMenu::class.java,
//                VOMenu::class.java,
//            )
//    }

}