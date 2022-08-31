package n7.ad2.logger

import com.google.common.truth.Truth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.coroutines.DispatchersProviderFake
import org.junit.Rule
import org.junit.Test

internal class AD2LoggerTest {

    @get:Rule val coroutineRule = CoroutineTestRule()
    private val dispatchers = DispatchersProviderFake()
    private val logger = AD2Logger(TestScope(), dispatchers)

    @Test
    fun `when send log and logger have zero subscribers should return log`() = runTest {
        logger.log("hello")
        val activeSubscription = logger.getSubscriptionCount().value
        Truth.assertThat(activeSubscription).isEqualTo(0)

        val result = logger.getLogFlow().first().message
        Truth.assertThat(result).isEqualTo("hello")
    }

}