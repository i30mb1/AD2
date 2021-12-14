package n7.ad2.ui.splash.domain.usecase

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import n7.ad2.CoroutineTestRule
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@SmallTest
class GetRandomEmoteUseCaseTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    val getRandomEmoteUseCase = GetRandomEmoteUseCase(coroutineTestRule.testDispatchers)

    @Test
    fun `random emote not empty`() = runTest {
        val emote = getRandomEmoteUseCase()
        assertThat(emote).isNotEmpty()
    }

}