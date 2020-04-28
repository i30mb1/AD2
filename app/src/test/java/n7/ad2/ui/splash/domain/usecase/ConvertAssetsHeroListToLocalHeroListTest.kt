package n7.ad2.ui.splash.domain.usecase

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import n7.ad2.CoroutineTestRule
import n7.ad2.data.source.local.model.AssetsHero
import n7.ad2.runBlockingTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@SmallTest
class ConvertAssetsHeroListToLocalHeroListTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val convertAssetsHeroesFromJsonUseCase = ConvertAssetsHeroListToLocalHeroList()

    @Test
    fun `fields of final object not empty`() = coroutineTestRule.runBlockingTest {
        val assetsHeroList = listOf(AssetsHero("Naruto", "Kanoha", "rofl"))

        val localHero = convertAssetsHeroesFromJsonUseCase(assetsHeroList)[0]

        assertThat(localHero.assetsPath).isNotNull()
        assertThat(localHero.name).isNotNull()
        assertThat(localHero.mainAttr).isNotNull()
    }

}