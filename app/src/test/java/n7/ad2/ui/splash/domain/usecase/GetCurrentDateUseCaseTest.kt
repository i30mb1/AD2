package n7.ad2.ui.splash.domain.usecase

import androidx.test.filters.FlakyTest
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import n7.ad2.CoroutineTestRule
import n7.ad2.runBlockingTest
import org.junit.Rule
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
@SmallTest
@FlakyTest // we should not test Data classes that why we mocked Calendar
class GetCurrentDateUseCaseTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    val getCurrentDateUseCase = GetCurrentDateInYearUseCase(coroutineTestRule.testDispatcher, mock())

    @Test
    fun `current day not null`() = coroutineTestRule.runBlockingTest {
        val day = getCurrentDateUseCase(Date(0))
        assertThat(day).isNotNull()
    }

    @Test
    fun `current day in diapason 0 to 366`() = coroutineTestRule.runBlockingTest {
        val day = getCurrentDateUseCase(Date(Long.MAX_VALUE))
        assertThat(day).isIn(0..366)

        getCurrentDateUseCase(Date(Long.MAX_VALUE))
        assertThat(day).isIn(0..366)
    }

    @Test
    fun `current day with expected result 1`() = coroutineTestRule.runBlockingTest {
        val calendar = Calendar.getInstance()
        calendar.clear()
        val expectedDay = 1
        val actualDay = getCurrentDateUseCase(calendar.time)
        assertThat(actualDay).isEqualTo(expectedDay)
    }

}