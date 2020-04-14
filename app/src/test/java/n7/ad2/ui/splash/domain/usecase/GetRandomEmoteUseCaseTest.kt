package n7.ad2.ui.splash.domain.usecase

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import n7.ad2.CoroutineTestRule
import n7.ad2.runBlockingTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@SmallTest
class GetRandomEmoteUseCaseTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    val getRandomEmoteUseCase = GetRandomEmoteUseCase(coroutineTestRule.testDispatcher)

    @Test
    fun `random emote not empty`() = coroutineTestRule.runBlockingTest {
        val emote = getRandomEmoteUseCase()
        assertThat(emote).isNotEmpty()
    }

}