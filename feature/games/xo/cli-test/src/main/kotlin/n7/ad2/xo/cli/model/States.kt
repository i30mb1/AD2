package n7.ad2.xo.cli.model

data class ServerState(
    val status: ServerStatus = ServerStatus.Closed,
    val messages: List<Message> = emptyList(),
)

sealed interface ServerStatus {
    data class Waiting(val server: SimpleServer) : ServerStatus
    data class Connected(val server: SimpleServer) : ServerStatus
    data object Closed : ServerStatus
}

data class ClientState(
    val status: ClientStatus = ClientStatus.Disconnected,
    val messages: List<Message> = emptyList(),
)

sealed interface ClientStatus {
    data object Disconnected : ClientStatus
    data class Connected(val server: SimpleServer) : ClientStatus
}

sealed class Message(val text: String) {
    class Me(text: String) : Message(text)
    class Other(text: String) : Message(text)
    class Info(text: String) : Message(text)
}

data class SimpleServer(
    val name: String,
    val ip: String,
    val port: Int,
)