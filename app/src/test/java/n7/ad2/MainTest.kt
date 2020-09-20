package n7.ad2

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertWithMessage
import com.squareup.moshi.Moshi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import n7.ad2.ui.splash.domain.usecase.ConvertJsonHeroesToAssetsHeroesUseCase
import org.junit.Rule
import org.junit.Test
import java.io.File

@ExperimentalCoroutinesApi
@SmallTest
class MainTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val moshi = Moshi.Builder().build()

    val convertJsonHeroesToAssetsHeroesUseCase = ConvertJsonHeroesToAssetsHeroesUseCase(coroutineTestRule.testDispatcher, moshi)

    @Test
    fun `hero file exist and not empty`() {
        `file exist and not empty`("heroes.json")

        `all heroes have english description`("ru")
        `all heroes have english description`("en")
    }

    @Test
    fun `item file exist and not empty`() {
        `file exist and not empty`("items.json")

    }

    private fun `file exist and not empty`(path: String) {
        val file = File("${System.getProperty("user.dir")}\\src\\main\\assets\\$path")
        assertWithMessage("heroes file not Exist").that(file.exists()).isTrue()

        val text = file.readText()
        assertWithMessage("heroes file not ready! Run ParserInfo").that(text).isNotEmpty()
    }


    private fun `all heroes have english description`(locale: String) = coroutineTestRule.runBlockingTest {
        val heroesJson = File("${System.getProperty("user.dir")}\\src\\main\\assets\\heroes.json").readText()
        val heroes = convertJsonHeroesToAssetsHeroesUseCase(heroesJson)

        val path = "${System.getProperty("user.dir")}\\src\\main\\assets\\"
        heroes.forEach {
            val file = File("$path${it.assetsPath}\\$locale\\description.json")

            assertWithMessage("description file not exist for ${it.name}").that(file.exists()).isTrue()

            assertWithMessage("description file empty for ${it.name}").that(file.readText()).isNotEmpty()
        }

    }

}