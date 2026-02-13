package n7.ad2.feature.games.xo.domain.internal.server.data

import n7.ad2.feature.games.xo.domain.model.SimpleServer

data class ServerState(val status: ServerStatus = ServerStatus.Closed, val messages: List<Message> = emptyList())

sealed interface ServerStatus {
    data class Waiting(val server: SimpleServer) : ServerStatus
    data class Connected(val server: SimpleServer) : ServerStatus
    data object Closed : ServerStatus
}

sealed class Message(val text: String) {
    class Me(text: String) : Message(text)
    class Other(text: String) : Message(text)
    class Info(text: String) : Message(text)
}
