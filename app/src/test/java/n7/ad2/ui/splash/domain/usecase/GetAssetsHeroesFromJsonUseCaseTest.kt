package n7.ad2.ui.splash.domain.usecase

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.squareup.moshi.Moshi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import n7.ad2.CoroutineTestRule
import n7.ad2.data.source.local.model.AssetsHero
import n7.ad2.runBlockingTest
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException

@ExperimentalCoroutinesApi
@SmallTest
class GetAssetsHeroesFromJsonUseCaseTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val moshi = Moshi.Builder().build()

    val getLocalHeroesFromFileUseCase = GetAssetsHeroesFromJsonUseCase(coroutineTestRule.testDispatcher, moshi)

    @Test(expected = FileNotFoundException::class)
    fun `if file empty throw error`() = coroutineTestRule.runBlockingTest {
        val jsonString = ""

        getLocalHeroesFromFileUseCase(jsonString)
    }

    @Test
    fun `return object with fields from json`() = coroutineTestRule.runBlockingTest {
        val testObject = AssetsHero("name", "assetsPath", "mainAttr")
        val jsonString = """
                {
                "heroes": [
                    {
                      "nameEng": "${testObject.name}",
                      "assetsPath": "${testObject.assetsPath}",
                      "mainAttr": "${testObject.mainAttr}"
                    }
                          ] 
                }
        """.trimIndent()

        val list = getLocalHeroesFromFileUseCase(jsonString)

        assertThat(list[0]).isEqualTo(testObject)
    }
}