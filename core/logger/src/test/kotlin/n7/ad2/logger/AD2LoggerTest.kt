package n7.ad2.logger

import com.google.common.truth.Truth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import org.junit.Rule
import org.junit.Test

internal class AD2LoggerTest {

    @get:Rule val coroutineRule = CoroutineTestRule()
    private val logger = AD2Logger(TestScope(), coroutineRule.dispatchers)

    @Test
    fun `when send log and logger have zero subscribers should return log`() = runTest {
        logger.log("hello")
        val activeSubscription = logger.getSubscriptionCount()
        Truth.assertThat(activeSubscription).isEqualTo(0)

        val result = logger.getLogFlow().first().message
        Truth.assertThat(result).isEqualTo("hello")
    }

    @Test
    fun sdf() = runTest {
        val values = mutableListOf<AD2Log>()
        val flow = launch(UnconfinedTestDispatcher()) {
            logger.getLogFlow().toList(values)
        }
        logger.log("hello")
        Truth.assertThat(values.last().message).isEqualTo("hello")
        flow.cancel()
    }

}