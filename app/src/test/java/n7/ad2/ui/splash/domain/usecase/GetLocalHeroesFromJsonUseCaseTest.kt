package n7.ad2.ui.splash.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import n7.ad2.CoroutineTestRule
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.runBlockingTest
import org.junit.Rule
import org.junit.Test
import java.io.FileNotFoundException

@ExperimentalCoroutinesApi
class GetLocalHeroesFromJsonUseCaseTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val moshi = Moshi.Builder().build()

    val getLocalHeroesFromFileUseCase = GetLocalHeroesFromJsonUseCase(coroutineTestRule.testDispatcher, moshi)

    @Test(expected = FileNotFoundException::class)
    fun `if file empty throw error`() = coroutineTestRule.runBlockingTest {
        val jsonString = ""

        getLocalHeroesFromFileUseCase(jsonString)
    }

    @Test
    fun `return object with fields from json`() = coroutineTestRule.runBlockingTest {
        val testObject = LocalHero("Abaddon", "heroes2\\Abaddon", "Strength")
        val jsonString = """
                {
                  "heroes": [
                    {
                      "nameEng": ${testObject.name}",
                      "assetsPath": ${testObject.assetsPath},
                      "mainAttr": ${testObject.mainAttr}
                    }
                            ] 
                }
        """.trimIndent()

        val list = getLocalHeroesFromFileUseCase(jsonString)[0]
        assertThat(list).isEqualTo(testObject)
    }
}