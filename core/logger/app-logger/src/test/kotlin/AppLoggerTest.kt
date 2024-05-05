
import com.google.common.truth.Truth
import kotlinx.coroutines.debug.junit4.CoroutinesTimeout
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import n7.ad2.app.logger.Logger
import n7.ad2.app.logger.model.AppLog
import org.junit.Rule
import org.junit.Test

internal class AppLoggerTest {

    @get:Rule val timeout = CoroutinesTimeout.seconds(5)
    private val logger = Logger()

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
        logger.getLogFlow()
            .onEach { values.add(it) }
            .launchIn(backgroundScope + UnconfinedTestDispatcher())
        logger.log("Message1")
        logger.log("Message2")
        Truth.assertThat(values)
            .containsExactly(
                AppLog("Message1"),
                AppLog("Message2"),
            )
    }
}
