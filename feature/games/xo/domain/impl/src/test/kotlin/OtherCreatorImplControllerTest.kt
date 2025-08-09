import com.google.common.truth.Truth
import java.net.InetAddress
import kotlinx.coroutines.debug.junit4.CoroutinesTimeout
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.feature.games.xo.domain.internal.server.controller.SocketClientController
import n7.ad2.feature.games.xo.domain.internal.server.controller.SocketServerController
import n7.ad2.feature.games.xo.domain.internal.server.data.ClientStatus
import n7.ad2.feature.games.xo.domain.internal.server.data.ServerStatus
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Проверяем поведение ServerSocket с общением по Socket
 */
@RunWith(Parameterized::class)
internal class OtherCreatorImplControllerTest(
    private val param: Boolean,
) {

    @get:Rule val timeout = CoroutinesTimeout.seconds(5000)
    @get:Rule val coroutineRule = CoroutineTestRule(StandardTestDispatcher())
    private val host = InetAddress.getLoopbackAddress()
    private val server = SocketServerController()
    private val client = SocketClientController()

    @Test
    fun `WHEN client send message to server THEN server receive message`() = runTest {
        // запускаем сервер
        val serverName = "Test"
        server.start(serverName, host)
        val state = server.state.first { it.status is ServerStatus.Waiting }.status as ServerStatus.Waiting

        // запускаем клиента
        val port = state.server.port
        client.connect(serverName, host, port)

        // дожидаемся клиента на сервере
        client.state.mapNotNull { it.status as? ClientStatus.Connected }.first()
        val message = "x:0-0:1"
        client.send(message)

        val clientMessageOnServer = server.state
            .filter { it.messages.isNotEmpty() }
            .first()
            .messages
            .first()
            .text
        Truth.assertThat(clientMessageOnServer).isEqualTo(message)
    }

    @Test
    fun `WHEN server send message to client THEN client receive message`() = runTest {
        // запускаем сервер
        val serverName = "Test"
        server.start(serverName, host)
        val state = server.state.first { it.status is ServerStatus.Waiting }.status as ServerStatus.Waiting

        // запускаем клиента
        val port = state.server.port
        client.connect(serverName, host, port)

        // дожидаемся клиента на сервере
        client.state.mapNotNull { it.status as? ClientStatus.Connected }.first()
        // отправляем сообщение
        val message = "x:0-0:1"
        server.send(message)

        val serverMessageOnClient = client.state.filter { it.messages.isNotEmpty() }.first().messages.first().text
        Truth.assertThat(serverMessageOnClient).isEqualTo(message)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun build(): Collection<Boolean> {
            return listOf(true)
        }
    }
}
