package n7.ad2.xo.internal.game

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import n7.ad2.feature.games.xo.domain.model.Server

internal data class GameState(
    val deviceIP: String,
    val deviceName: String,
    val port: String,
    val servers: List<Server> = emptyList(),
    val serverState: ServerState = ServerState.Disconected,
    val messages: List<Message> = emptyList(),
) {

    companion object {
        fun init() = GameState(
            "",
            "",
            "",
        )
    }
}

internal sealed class Message(val message: String) {
    class Client(message: String) : Message(message)
    class Server(message: String) : Message(message)
}

internal sealed interface ServerState {
    data class Connected(val server: Server, val isHost: Boolean) : ServerState
    data class Connecting(val server: Server) : ServerState
    data object Disconected : ServerState
}

internal fun MutableStateFlow<GameState>.setDeviceIP(ip: String) = update { it.copy(deviceIP = ip) }

internal fun MutableStateFlow<GameState>.setServerState(state: ServerState) = update { it.copy(serverState = state) }

internal fun MutableStateFlow<GameState>.setDeviceName(name: String) = update { it.copy(deviceName = name) }

internal fun MutableStateFlow<GameState>.setServers(servers: List<Server>) = update { it.copy(servers = servers) }

internal fun MutableStateFlow<GameState>.addClientMessage(message: String) = update { it.copy(messages = it.messages + Message.Client(message)) }

internal fun MutableStateFlow<GameState>.addServerMessage(message: String) = update { it.copy(messages = it.messages + Message.Server(message)) }
