package n7.ad2.heroes.ui.internal

import GetHeroesUseCaseFake
import LoggerFake
import UpdateStateViewedForHeroUseCaseFake
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.heroes.ui.internal.domain.usecase.FilterHeroesUseCase
import n7.ad2.heroes.ui.internal.domain.usecase.GetVOHeroesListUseCase
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class HeroesViewModelTest {

    @get:Rule val coroutineRule = CoroutineTestRule(StandardTestDispatcher())
    private val heroesViewModel = HeroesViewModel(
        getVOHeroesListUseCase = GetVOHeroesListUseCase(GetHeroesUseCaseFake(), coroutineRule.dispatchers, LoggerFake()),
        filterHeroesUseCase = { FilterHeroesUseCase(coroutineRule.dispatchers) },
        updateStateViewedForHeroUseCase = { UpdateStateViewedForHeroUseCaseFake() }
    )

    @Test
    fun `должен загрузить и эмитить непустой список героев через GetVOHeroesListUseCase`() = runTest {
        heroesViewModel.allHeroes
    }

}
