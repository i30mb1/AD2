package n7.ad2.ui.splash.domain.usecase

import com.squareup.moshi.Moshi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import n7.ad2.CoroutineTestRule
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

}