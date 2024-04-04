
import com.google.common.truth.Truth
import io.mockk.mockk
import java.net.InetAddress
import kotlinx.coroutines.debug.junit4.CoroutinesTimeout
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.feature.games.xo.domain.internal.server.socket.ClientHolderWithSocket
import n7.ad2.feature.games.xo.domain.internal.server.socket.ServerHolderWithSocket
import n7.ad2.feature.games.xo.domain.internal.server.socket.SocketHolderImpl
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Проверяем поведение ServerSocket с общением по Socket
 */
@RunWith(Parameterized::class)
internal class ServerHolderWithSocketTest(
    private val param: Boolean,
) {

    @get:Rule val timeout = CoroutinesTimeout.seconds(5)
    @get:Rule val coroutineRule = CoroutineTestRule(StandardTestDispatcher())
    private val serverHolder = ServerHolderWithSocket(mockk(relaxed = true))
    private val clientHolder = ClientHolderWithSocket()
    private val serverSocket = SocketHolderImpl()
    private val clientSocket = SocketHolderImpl()

    @Test
    fun `WHEN client send message to server THEN server receive message`() = runTest {
        startServerAndConnectClient()

        val message = "x:0-0:1"
        clientSocket.sendMessage(message)

        val clientMessageOnServer = serverSocket.awaitMessage()
        Truth.assertThat(clientMessageOnServer).isEqualTo(message)
    }

    @Test
    fun `WHEN server send message to client THEN client receive message`() = runTest {
        startServerAndConnectClient()

        val message = "y:0-0:1"
        serverSocket.sendMessage(message)

        val clientMessageOnServer = clientSocket.awaitMessage()
        Truth.assertThat(clientMessageOnServer).isEqualTo(message)
    }

    private suspend fun startServerAndConnectClient() {
        val host = InetAddress.getLocalHost()
        val server = serverHolder.start(host, "Test")
        val port = server.localPort
        clientSocket.socket = clientHolder.start(host, port)
        serverSocket.socket = serverHolder.awaitClient()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun build(): Collection<Boolean> {
            return listOf(
                true,
                false,
            )
        }
    }
}

//produce<String> {
//    send("")
//}
//select<String> {
//
//}
