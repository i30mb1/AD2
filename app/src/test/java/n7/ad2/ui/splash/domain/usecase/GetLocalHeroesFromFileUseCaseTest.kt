package n7.ad2.ui.splash.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import n7.ad2.CoroutineTestRule
import org.junit.Assert.*
import org.junit.Rule

@ExperimentalCoroutinesApi
class GetLocalHeroesFromFileUseCaseTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    val getLocalHeroesFromFileUseCase = GetLocalHeroesFromFileUseCase(coroutineTestRule.testDispatcher)

}