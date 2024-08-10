package n7.ad2.feature.games.xo.domain.internal.server2.data

import n7.ad2.feature.games.xo.domain.model.SimpleServer

data class ClientState(
    val status: ClientStatus = ClientStatus.Disconnected,
    val messages: List<Message> = emptyList(),
)

sealed interface ClientStatus {
    data object Disconnected : ClientStatus
    data class Connected(val server: SimpleServer) : ClientStatus
}
