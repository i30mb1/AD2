package n7.ad2.games.room.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import n7.ad2.feature.games.xo.domain.internal.server.controller.ServerController
import n7.ad2.feature.games.xo.domain.internal.server.data.ServerStatus
import java.net.InetAddress

internal class WebsocketHostTransport(private val server: ServerController, scope: CoroutineScope) : Transport {

    // до start() контроллер в Closed - это не закрытие, а "еще не запущен"
    private var wasOpen = false

    override val status: StateFlow<TransportStatus> = server.state
        .map { state ->
            when (val status = state.status) {
                is ServerStatus.Waiting -> TransportStatus.Listening(status.server.port).also { wasOpen = true }
                is ServerStatus.Connected -> TransportStatus.Connected.also { wasOpen = true }
                ServerStatus.Closed -> if (wasOpen) TransportStatus.Closed(failed = false) else TransportStatus.Connecting
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, TransportStatus.Connecting)

    override val incoming: Flow<String> = server.state.newOtherMessages { it.messages }

    fun start(name: String, bindAddress: InetAddress) = server.start(name, bindAddress, 0)

    override fun send(text: String) = server.send(text)

    override fun close() = server.stop()
}
