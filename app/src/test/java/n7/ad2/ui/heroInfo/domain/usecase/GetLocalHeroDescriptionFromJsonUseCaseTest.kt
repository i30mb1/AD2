package n7.ad2.ui.heroInfo.domain.usecase

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import n7.ad2.CoroutineTestRule
import n7.ad2.runBlockingTest
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@SmallTest
class GetLocalHeroDescriptionFromJsonUseCaseTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val moshi = Moshi.Builder().build()

    private val useCase = GetLocalHeroDescriptionFromJsonUseCase(coroutineTestRule.testDispatcher, moshi)

    @Test
    fun `out put file length more than 500 chars`() = coroutineTestRule.runBlockingTest {
        val result: LocalHeroDescription = useCase.invoke("Abbadon")

        assertThat(result.toString().length).isGreaterThan(500)
    }

}
