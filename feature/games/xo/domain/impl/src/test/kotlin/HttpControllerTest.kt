import com.google.common.truth.Truth
import java.net.InetAddress
import kotlinx.coroutines.debug.junit4.CoroutinesTimeout
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.feature.games.xo.domain.internal.server.controller.HttpServerController
import n7.ad2.feature.games.xo.domain.internal.server.data.ServerStatus
import org.junit.Rule
import org.junit.Test

internal class HttpControllerTest {

    @get:Rule val timeout = CoroutinesTimeout.seconds(5)
    @get:Rule val coroutineRule = CoroutineTestRule(StandardTestDispatcher())
    private val host = InetAddress.getLoopbackAddress()

    @Test
    fun `WHEN HTTP server starts THEN server state becomes Connected`() = runTest {
        val server = HttpServerController()
        val serverName = "HTTP-Test"
        
        server.start(serverName, host, 0)
        val state = server.state.first { it.status is ServerStatus.Connected }.status as ServerStatus.Connected

        Truth.assertThat(state.server.name).isEqualTo(serverName)
        Truth.assertThat(state.server.port).isGreaterThan(0)

        server.stop()
        val finalState = server.state.first { it.status is ServerStatus.Closed }
        Truth.assertThat(finalState.status).isEqualTo(ServerStatus.Closed)
    }

    @Test 
    fun `WHEN HTTP server sends message THEN message is queued`() = runTest {
        val server = HttpServerController()
        val serverName = "HTTP-Test"
        
        server.start(serverName, host, 0)
        server.state.first { it.status is ServerStatus.Connected }
        
        val message = "test-message"
        server.send(message)
        
        val state = server.state.value
        Truth.assertThat(state.messages).hasSize(1)
        Truth.assertThat(state.messages[0].text).isEqualTo(message)
        
        server.stop()
    }
}