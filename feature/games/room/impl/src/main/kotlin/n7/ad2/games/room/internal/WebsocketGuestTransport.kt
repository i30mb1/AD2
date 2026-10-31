package n7.ad2.games.room.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import n7.ad2.feature.games.xo.domain.internal.server.controller.ClientController
import n7.ad2.feature.games.xo.domain.internal.server.data.ClientStatus
import n7.ad2.feature.games.xo.domain.internal.server.data.Message
import java.net.InetAddress

internal class WebsocketGuestTransport(private val client: ClientController, scope: CoroutineScope) : Transport {

    // до connect() и во время подключения контроллер в Disconnected - это еще не разрыв
    private var wasConnected = false

    override val status: StateFlow<TransportStatus> = client.state
        .map { state ->
            when {
                state.status is ClientStatus.Connected -> TransportStatus.Connected.also { wasConnected = true }
                wasConnected -> TransportStatus.Closed(failed = false)
                state.messages.any { it is Message.Info && it.text.startsWith(CONNECT_FAILED) } -> TransportStatus.Closed(failed = true)
                else -> TransportStatus.Connecting
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, TransportStatus.Connecting)

    override val incoming: Flow<String> = client.state.newOtherMessages { it.messages }

    fun connect(name: String, host: InetAddress, port: Int) = client.connect(name, host, port)

    override fun send(text: String) = client.send(text)

    override fun close() = client.disconnect()

    private companion object {
        /** префикс сообщения, которое пишет WebsocketClientController при неудачном подключении */
        const val CONNECT_FAILED = "Connect failed"
    }
}
