package n7.ad2.feature.games.xo.domain.internal.server.data

import n7.ad2.feature.games.xo.domain.model.SimpleServer

data class ServerState(
    val status: ServerStatus = ServerStatus.Closed,
    val messages: List<Message> = emptyList(),
)

sealed interface ServerStatus {
    data class Waiting(val server: SimpleServer) : ServerStatus
    data class Connected(val server: SimpleServer) : ServerStatus
    data object Closed : ServerStatus
}

sealed class Message(val message: String) {
    class Me(message: String) : Message(message)
    class Other(message: String) : Message(message)
    class Info(message: String) : Message(message)
}
