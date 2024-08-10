package n7.ad2.feature.games.xo.domain.internal.server

sealed interface ServerLog {
    data class ServerStarted(val name: String, val ip: String, val port: Int) : ServerLog
    data object ConnectionAccepted : ServerLog
    data class UnknownError(val exception: Exception) : ServerLog
    data class ReceiveMessage(val text: String) : ServerLog
    object StartFailed : ServerLog
    class PortBusy(val port: Int) : ServerLog
}

sealed interface ClientLog {
    object ClientSendingMessageError: ClientLog
}
