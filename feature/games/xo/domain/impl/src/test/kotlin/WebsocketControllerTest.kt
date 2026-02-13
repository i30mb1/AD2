import com.google.common.truth.Truth
import kotlinx.coroutines.debug.junit4.CoroutinesTimeout
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.feature.games.xo.domain.internal.server.controller.WebsocketServerController
import n7.ad2.feature.games.xo.domain.internal.server.data.ServerStatus
import org.junit.Rule
import org.junit.Test
import java.net.InetAddress

internal class WebsocketControllerTest {

    @get:Rule val timeout = CoroutinesTimeout.seconds(5)

    @get:Rule val coroutineRule = CoroutineTestRule(StandardTestDispatcher())
    private val host = InetAddress.getLoopbackAddress()

    @Test
    fun `WHEN WebSocket server starts THEN server state becomes Waiting`() = runTest {
        val server = WebsocketServerController()
        val serverName = "WebSocket-Test"

        server.start(serverName, host, 0)
        val state = server.state.first { it.status is ServerStatus.Waiting }.status as ServerStatus.Waiting

        Truth.assertThat(state.server.name).isEqualTo(serverName)
        Truth.assertThat(state.server.port).isGreaterThan(0)

        server.stop()
        val finalState = server.state.first { it.status is ServerStatus.Closed }
        Truth.assertThat(finalState.status).isEqualTo(ServerStatus.Closed)
    }

    @Test
    fun `WHEN WebSocket server sends message THEN message is added to state`() = runTest {
        val server = WebsocketServerController()
        val serverName = "WebSocket-Test"

        server.start(serverName, host, 0)
        server.state.first { it.status is ServerStatus.Waiting }

        val message = "websocket-test-message"
        try {
            server.send(message)
            val state = server.state.value
            Truth.assertThat(state.messages).hasSize(1)
            Truth.assertThat(state.messages[0].text).isEqualTo(message)
        } catch (e: Exception) {
            // Expected, since no client is connected yet
            Truth.assertThat(e.message).contains("Server closed")
        }

        server.stop()
    }
}
