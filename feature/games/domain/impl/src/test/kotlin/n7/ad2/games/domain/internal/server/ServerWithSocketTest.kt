package n7.ad2.games.domain.internal.server

import com.google.common.truth.Truth
import java.net.InetAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.games.domain.internal.server.base.ClientSocketProxy
import n7.ad2.games.domain.internal.server.base.ServerSocketProxy
import n7.ad2.games.domain.internal.server.socket.ClientWithSocket
import n7.ad2.games.domain.internal.server.socket.ServerWithSocket
import org.junit.Rule
import org.junit.Test

/**
 * Проверяем поведение ServerSocket с общением по Socket
 */
class ServerWithSocketTest {

    @get:Rule val coroutineRule = CoroutineTestRule(StandardTestDispatcher())
    private val server = ServerWithSocket(ServerSocketProxy(), coroutineRule.dispatchers)
    private val client = ClientWithSocket(ClientSocketProxy())

    private val host = InetAddress.getLocalHost()
    private val port = 8080
    private val ports = intArrayOf(port)

    @Test
    fun `WHEN client send message to server THEN server receive message`() = runTest {
        server.start(host, ports)
        server.awaitStart()

        client.start(host, port)
        server.awaitClient()

        val clientMessage = "x:0-0:1"
        client.sendMessage(clientMessage)

        val clientMessageOnServer = server.awaitMessage()
        Truth.assertThat(clientMessageOnServer).isEqualTo(clientMessage)
    }
}
