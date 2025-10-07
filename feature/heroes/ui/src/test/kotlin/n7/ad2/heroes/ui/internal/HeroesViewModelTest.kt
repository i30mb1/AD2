package n7.ad2.heroes.ui.internal

import GetHeroesUseCaseFake
import LoggerFake
import UpdateStateViewedForHeroUseCaseFake
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.heroes.ui.internal.domain.usecase.FilterHeroesUseCase
import n7.ad2.heroes.ui.internal.domain.usecase.GetVOHeroesListUseCase
import n7.ad2.heroes.ui.internal.domain.vo.VOHero
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class HeroesViewModelTest {

    @get:Rule val coroutineRule = CoroutineTestRule()

    private fun createViewModel() = HeroesViewModel(
        getVOHeroesListUseCase = GetVOHeroesListUseCase(GetHeroesUseCaseFake(), coroutineRule.dispatchers, LoggerFake()),
        filterHeroesUseCase = { FilterHeroesUseCase(coroutineRule.dispatchers) },
        updateStateViewedForHeroUseCase = { UpdateStateViewedForHeroUseCaseFake() }
    )

    @Test
    fun `должен вернуть героев при инициализации с пустым фильтром`() = coroutineRule.testScope.runTest {
        val heroesViewModel = createViewModel()

        val heroes = heroesViewModel.heroes.value
        assertThat(heroes).isNotEmpty()
    }

    @Test
    fun `должен загрузить список героев с непустым фильтром`() = coroutineRule.testScope.runTest {
        val heroesViewModel = createViewModel()

        heroesViewModel.filterHeroes("a")

        val heroes = heroesViewModel.heroes.value
        assertThat(heroes).isNotEmpty()

        val bodyHeroes = heroes.filterIsInstance<VOHero.Body>()
        assertThat(bodyHeroes).hasSize(3)
        assertThat(bodyHeroes.map { it.name }).containsExactly("Anti-Mage", "Axe", "Crystal Maiden")
    }

    @Test
    fun `должен фильтровать героев по имени`() = coroutineRule.testScope.runTest {
        val heroesViewModel = createViewModel()

        heroesViewModel.filterHeroes("Axe")

        val filtered = heroesViewModel.heroes.value
        val bodyHeroes = filtered.filterIsInstance<VOHero.Body>()
        assertThat(bodyHeroes).hasSize(1)
        assertThat(bodyHeroes.first().name).isEqualTo("Axe")
    }

    @Test
    fun `должен вернуть всех героев при пустом фильтре`() = coroutineRule.testScope.runTest {
        val heroesViewModel = createViewModel()

        heroesViewModel.filterHeroes("")

        val heroes = heroesViewModel.heroes.value
        assertThat(heroes).isNotEmpty()
    }

    @Test
    fun `должен обновить героев при вызове refresh`() = coroutineRule.testScope.runTest {
        val heroesViewModel = createViewModel()

        heroesViewModel.filterHeroes("Anti")
        val heroesBefore = heroesViewModel.heroes.value
        assertThat(heroesBefore.size).isEqualTo(1)

        heroesViewModel.refresh()

        val heroesAfter = heroesViewModel.heroes.value
        assertThat(heroesAfter.size).isEqualTo(1)
    }

}
