package n7.ad2.feature.games.xo.domain.internal.server

sealed interface ServerLog {
    object ServerStarted : ServerLog
    object ConnectionAccepted : ServerLog
    class UnknownError(val exception: Exception) : ServerLog
    class ReceiveMessage(val text: String) : ServerLog
    object PortsBusy : ServerLog
    class PortBusy(val port: Int) : ServerLog
}

sealed interface ClientLog {
    object ClientSendingMessageError: ClientLog
}
