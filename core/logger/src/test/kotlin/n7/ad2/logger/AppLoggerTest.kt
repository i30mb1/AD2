package n7.ad2.logger

import com.google.common.truth.Truth
import kotlinx.coroutines.debug.junit4.CoroutinesTimeout
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import n7.ad2.AppMetrics
import n7.ad2.coroutines.CoroutineTestRule
import org.junit.Rule
import org.junit.Test

internal class AppLoggerTest {

    @get:Rule val coroutineRule = CoroutineTestRule()
    @get:Rule val timeout = CoroutinesTimeout.seconds(5)
    private val logger = AppLogger(AppMetricsFake())

    @Test
    fun `when send log and logger have zero subscribers should return log`() = runTest {
        logger.log("hello")
        val activeSubscription = logger.getSubscriptionCount()
        Truth.assertThat(activeSubscription).isEqualTo(0)

        val result = logger.getLogFlow().first().message
        Truth.assertThat(result).isEqualTo("hello")
    }

    @Test
    fun `when send log receive it`() = runTest {
        val values = mutableListOf<AppLog>()
        val flow = launch(UnconfinedTestDispatcher()) {
            logger.getLogFlow().toList(values)
        }
        logger.log("hello")
        Truth.assertThat(values.last().message).isEqualTo("hello")
        flow.cancel()
    }

}

private class AppMetricsFake : AppMetrics {
    override fun logEvent(event: String, params: Map<String, Any>) = Unit
}