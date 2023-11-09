import com.google.common.truth.Truth
import java.net.InetAddress
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.feature.games.xo.domain.internal.server.base.ClientSocketProxy
import n7.ad2.feature.games.xo.domain.internal.server.base.ServerSocketProxy
import n7.ad2.feature.games.xo.domain.internal.server.socket.ClientHolderWithSocket
import n7.ad2.feature.games.xo.domain.internal.server.socket.ServerHolderWithSocket
import org.junit.Rule
import org.junit.Test

/**
 * Проверяем поведение ServerSocket с общением по Socket
 */
internal class ServerHolderWithSocketTest {

//    @get:Rule val timeout = CoroutinesTimeout.seconds(5)
    @get:Rule val coroutineRule = CoroutineTestRule(StandardTestDispatcher())
    private val serverHolder = ServerHolderWithSocket(ServerSocketProxy())
    private val client = ClientHolderWithSocket(ClientSocketProxy())

    private val host = InetAddress.getLocalHost()

    @Test
    fun `WHEN client send message to server THEN server receive message`() = runTest {
        startServerAndConnectClient(8081)

        val message = "x:0-0:1"
        client.sendMessage(message)

        val clientMessageOnServer = serverHolder.awaitMessage()
        Truth.assertThat(clientMessageOnServer).isEqualTo(message)
    }

    @Test
    fun `WHEN server send message to client THEN client receive message`() = runTest {
        startServerAndConnectClient(8082)

        val message = "y:0-0:1"
        serverHolder.sendMessage(message)

        val clientMessageOnServer = client.awaitMessage()
        Truth.assertThat(clientMessageOnServer).isEqualTo(message)
    }

    private suspend fun startServerAndConnectClient(port: Int) {
        serverHolder.start(host, intArrayOf(port))
        client.start(host, port)
        serverHolder.awaitClient()
    }
}

//produce<String> {
//    send("")
//}
//select<String> {
//
//}
