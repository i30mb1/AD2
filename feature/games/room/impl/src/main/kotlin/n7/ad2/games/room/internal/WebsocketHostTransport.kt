package n7.ad2.games.room.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import n7.ad2.feature.games.xo.domain.internal.server.controller.WebsocketHub
import java.net.InetAddress

internal class WebsocketHostTransport(private val hub: WebsocketHub, scope: CoroutineScope) : HostTransport {

    override val status: StateFlow<HostStatus> = hub.status
        .map { status ->
            when (status) {
                WebsocketHub.Status.Starting -> HostStatus.Starting
                is WebsocketHub.Status.Listening -> HostStatus.Listening(status.port)
                is WebsocketHub.Status.Stopped -> HostStatus.Stopped(status.failed)
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, HostStatus.Starting)

    override val events: Flow<HostEvent> = hub.events.map { event ->
        when (event) {
            is WebsocketHub.Event.Opened -> HostEvent.Joined(event.client)
            is WebsocketHub.Event.Text -> HostEvent.Text(event.client, event.text)
            is WebsocketHub.Event.Closed -> HostEvent.Left(event.client)
        }
    }

    private val outgoing = OutgoingQueue(scope)

    fun start(bindAddress: InetAddress) = hub.start(bindAddress)

    override fun send(client: Int, text: String) = outgoing.post { hub.send(client, text) }

    override fun drop(client: Int) = outgoing.post { hub.close(client) }

    override fun close() = outgoing.post { hub.stop() }
}
