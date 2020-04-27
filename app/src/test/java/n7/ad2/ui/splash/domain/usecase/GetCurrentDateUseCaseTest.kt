package n7.ad2.ui.splash.domain.usecase

import androidx.test.filters.FlakyTest
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
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

    companion object {
        const val YEAR = 0
        const val MONTH = 0
        const val DAY = 1
    }

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val testDate = Calendar.getInstance().apply {
        set(YEAR, MONTH, DAY)
    }

    val getCurrentDateInYearUseCase = GetCurrentDateInYearUseCase(coroutineTestRule.testDispatcher, testDate)

    @Test
    fun `current day not null`() = coroutineTestRule.runBlockingTest {
        getCurrentDateInYearUseCase().let {
            assertThat(it).isNotNull()
        }
    }

    @Test
    fun `current day in diapason 0 to 366`() = coroutineTestRule.runBlockingTest {
        getCurrentDateInYearUseCase(Date(Long.MAX_VALUE)).let {
            assertThat(it).isIn(0..366)
        }

        getCurrentDateInYearUseCase(Date(Long.MIN_VALUE)).let {
            assertThat(it).isIn(0..366)
        }
    }

    @Test
    fun `current day with expected result`() = coroutineTestRule.runBlockingTest {
        getCurrentDateInYearUseCase().let {
            assertThat(it).isEqualTo(DAY)
        }
    }

}