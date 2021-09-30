package n7.ad2.ui.splash.domain.usecase

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import n7.ad2.CoroutineTestRule
import n7.ad2.runBlockingTest
import n7.ad2.ui.splash.domain.model.AssetsHero
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@SmallTest
class ConvertAssetsHeroListToLocalHeroListUseCaseTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val convertAssetsHeroesFromJsonUseCase = ConvertAssetsHeroListToLocalHeroListUseCase()

    @Test
    fun `fields of final object not empty`() = coroutineTestRule.runBlockingTest {
        val assetsHeroList = listOf(AssetsHero("Naruto", "Kanoha", "rofl"))

        val localHero = convertAssetsHeroesFromJsonUseCase(assetsHeroList)[0]

        assertThat(localHero.name).isNotEmpty()
        assertThat(localHero.mainAttr).isNotEmpty()
    }

}