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
class GetCurrentDateUseCaseTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    val getCurrentDateUseCase = GetCurrentDateUseCase(coroutineTestRule.testDispatcher)

    @Test
    fun `current day not null`() = coroutineTestRule.runBlockingTest {
        val day = getCurrentDateUseCase()
        assertThat(day).isNotNull()
    }

    @Test
    fun `current day in diapason 0 to 366`() = coroutineTestRule.runBlockingTest {
        val day = getCurrentDateUseCase()
        assertThat(day).isIn(0..366)
    }

}