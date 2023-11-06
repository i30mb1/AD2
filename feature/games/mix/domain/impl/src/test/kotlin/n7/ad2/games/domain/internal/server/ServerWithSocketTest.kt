package n7.ad2.games.domain.internal.server

import com.google.common.truth.Truth
import java.net.InetAddress
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
    private val server = ServerWithSocket(ServerSocketProxy())
    private val client = ClientWithSocket(ClientSocketProxy())

    private val host = InetAddress.getLocalHost()

    @Test
    fun `WHEN client send message to server THEN server receive message`() = runTest {
        startServerAndConnectClient(8081)

        val message = "x:0-0:1"
        client.sendMessage(message)

        val clientMessageOnServer = server.awaitMessage()
        Truth.assertThat(clientMessageOnServer).isEqualTo(message)
    }

    @Test
    fun `WHEN server send message to client THEN client receive message`() = runTest {
        startServerAndConnectClient(8082)

        val message = "y:0-0:1"
        server.sendMessage(message)

        val clientMessageOnServer = client.awaitMessage()
        Truth.assertThat(clientMessageOnServer).isEqualTo(message)
    }

    private suspend fun startServerAndConnectClient(port: Int) {
        server.start(host, intArrayOf(port))
        client.start(host, port)
        server.awaitClient()
    }
}

//produce<String> {
//    send("")
//}
//select<String> {
//
//}
