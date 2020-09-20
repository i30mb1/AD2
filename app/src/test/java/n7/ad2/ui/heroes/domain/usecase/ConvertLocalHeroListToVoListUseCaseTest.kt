package n7.ad2.ui.heroes.domain.usecase

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import n7.ad2.CoroutineTestRule
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.runBlockingTest
import n7.ad2.ui.heroes.domain.vo.VOHero
import org.junit.Rule
import org.junit.Test
import kotlin.reflect.full.declaredMembers

@ExperimentalCoroutinesApi
@SmallTest
class ConvertLocalHeroListToVoListUseCaseTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val convertLocalHeroListToVoListUseCase = ConvertLocalHeroListToVoListUseCase(coroutineTestRule.testDispatcher)

    @Test
    fun `fields of final object not empty`() = coroutineTestRule.runBlockingTest {
        val hero = LocalHero(0,"Naruto", "path", "agility", false)
        val list = listOf(hero, hero)

        val voList: List<VOHero> = convertLocalHeroListToVoListUseCase.invoke(list)

        Truth.assertThat(voList[0].name).isNotEmpty()
        Truth.assertThat(voList[0].image).isNotEmpty()

    }

}