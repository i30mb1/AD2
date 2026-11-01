package n7.ad2.games.room.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import n7.ad2.feature.games.xo.domain.internal.server.controller.WebsocketClient
import java.net.InetAddress

internal class WebsocketGuestTransport(private val client: WebsocketClient, scope: CoroutineScope) : Transport {

    override val events: Flow<TransportEvent> = client.events.map { event ->
        when (event) {
            WebsocketClient.Event.Connected -> TransportEvent.Connected
            is WebsocketClient.Event.Text -> TransportEvent.Text(event.text)
            is WebsocketClient.Event.Closed -> TransportEvent.Closed(event.failed)
        }
    }

    private val outgoing = OutgoingQueue(scope)

    fun connect(host: InetAddress, port: Int) = client.connect(host, port)

    override fun send(text: String) = outgoing.post { client.send(text) }

    // после уже поставленного в очередь: Leave дойдет до хоста раньше, чем закроется соединение
    override fun close() = outgoing.post { client.close() }
}
