import com.google.common.truth.Truth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.debug.junit4.CoroutinesTimeout
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import n7.ad2.feature.games.xo.domain.internal.server.controller.WebsocketClientController
import n7.ad2.feature.games.xo.domain.internal.server.data.ClientStatus
import org.junit.Rule
import org.junit.Test
import java.net.InetAddress
import java.net.ServerSocket

internal class WebsocketClientControllerTest {

    @get:Rule val timeout = CoroutinesTimeout.seconds(5)

    @Test
    fun `WHEN connecting to a closed port THEN client stays Disconnected and reports the failure`() = runBlocking {
        val closedPort = ServerSocket(0).use { it.localPort }
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        val client = WebsocketClientController(scope)

        client.connect("closed", InetAddress.getLoopbackAddress(), closedPort)
        val state = client.state.first { it.messages.isNotEmpty() }

        Truth.assertThat(state.status).isEqualTo(ClientStatus.Disconnected)
        Truth.assertThat(state.messages.single().text).contains("Connect failed")
        scope.cancel()
    }
}
