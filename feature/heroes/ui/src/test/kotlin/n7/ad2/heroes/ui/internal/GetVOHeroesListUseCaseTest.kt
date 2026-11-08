package n7.ad2.heroes.ui.internal

import LoggerFake
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.feature.heroes.ui.internal.domain.usecase.GetVOHeroesListUseCase
import n7.ad2.feature.heroes.ui.internal.domain.vo.VOHero
import n7.ad2.heroes.domain.model.Hero
import n7.ad2.heroes.domain.model.HeroAttribute
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import org.junit.Rule
import org.junit.Test

internal class GetVOHeroesListUseCaseTest {

    @get:Rule val coroutineRule = CoroutineTestRule()

    // the database keeps the order rows were added in: the 2023 heroes by attribute, then the ones synced since
    private val heroes = listOf(
        Hero("Abaddon", "", false, "universal"),
        Hero("Axe", "", false, "Strength"),
        Hero("Anti-Mage", "", false, "Agility"),
        Hero("Zeus", "", false, "Intelligence"),
        Hero("Alchemist", "", false, "Strength"),
        Hero("Kez", "", false, "agility"),
        Hero("Arc Warden", "", false, "universal"),
    )

    private fun useCase() = GetVOHeroesListUseCase(
        object : GetHeroesUseCase {
            override fun invoke(): Flow<List<Hero>> = flowOf(heroes)
        },
        coroutineRule.dispatchers,
        LoggerFake(),
    )

    @Test
    fun `attributes go in the order of the game, heroes by name in each`() = coroutineRule.testScope.runTest {
        val list = useCase()().first()

        assertThat(list.map { item -> if (item is VOHero.Header) item.attribute else (item as VOHero.Body).name }).containsExactly(
            HeroAttribute.STRENGTH, "Alchemist", "Axe",
            HeroAttribute.AGILITY, "Anti-Mage", "Kez",
            HeroAttribute.INTELLIGENCE, "Zeus",
            HeroAttribute.UNIVERSAL, "Abaddon", "Arc Warden",
        ).inOrder()
    }
}
