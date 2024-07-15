
import com.google.common.truth.Truth
import io.mockk.mockk
import java.net.InetAddress
import kotlinx.coroutines.debug.junit4.CoroutinesTimeout
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.feature.games.xo.domain.internal.server.socket.ClientHolderWithSocket
import n7.ad2.feature.games.xo.domain.internal.server.socket.ServerHolderPlain
import n7.ad2.feature.games.xo.domain.internal.server.socket.SocketMessangerImpl
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Проверяем поведение ServerSocket с общением по Socket
 */
@RunWith(Parameterized::class)
internal class ServerHolderPlainTest(
    private val param: Boolean,
) {

    @get:Rule val timeout = CoroutinesTimeout.seconds(5)
    @get:Rule val coroutineRule = CoroutineTestRule(StandardTestDispatcher())
    private val host = InetAddress.getLoopbackAddress()
    private val serverHolder = ServerHolderPlain(mockk(relaxed = true))
    private val clientHolder = ClientHolderWithSocket()

    @Test
    fun `WHEN client send message to server THEN server receive message`() = runTest {
        val socket = serverHolder.start(host, "Test")
        val clientSocket = clientHolder.start(host, socket.localPort)
        val client = SocketMessangerImpl(clientSocket)
        val serverSocket = serverHolder.awaitClient()
        val server = SocketMessangerImpl(serverSocket)

        val message = "x:0-0:1"
        client.sendMessage(message)

        val clientMessageOnServer = server.awaitMessage()
        Truth.assertThat(clientMessageOnServer).isEqualTo(message)
    }

    @Test
    fun `WHEN server send message to client THEN client receive message`() = runTest {
        val socket = serverHolder.start(host, "Test")
        val clientSocket = clientHolder.start(host, socket.localPort)
        val client = SocketMessangerImpl(clientSocket)
        val serverSocket = serverHolder.awaitClient()
        val server = SocketMessangerImpl(serverSocket)

        val message = "y:0-0:1"
        server.sendMessage(message)

        val clientMessageOnServer = client.awaitMessage()
        Truth.assertThat(clientMessageOnServer).isEqualTo(message)
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
