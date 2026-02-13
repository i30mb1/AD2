import kotlinx.coroutines.debug.junit4.CoroutinesTimeout
import org.junit.Rule
import org.junit.Test

class StateFlowTest {

    @get:Rule val timeout = CoroutinesTimeout.seconds(5)

    @Test
    fun `simple state flow`() {
        Unit
    }
}
