package n7.ad2.games.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.net.InetAddress

sealed interface RoomState {
    data object Connecting : RoomState
    data class Waiting(val host: Player, val port: Int) : RoomState
    data class Ready(val host: Player, val guest: Player) : RoomState
    data class Playing(val host: Player, val guest: Player, val gameId: String, val mode: GameMode, val seed: Long) : RoomState
    data class Closed(val reason: CloseReason) : RoomState
}

enum class CloseReason { LEFT, PEER_LEFT, CONNECTION_FAILED, VERSION_MISMATCH }

interface Room {
    val me: Player
    val state: StateFlow<RoomState>

    /** События только второго игрока */
    val events: Flow<RoomMessage.GameEvent>

    /** Только хост в [RoomState.Ready], иначе [IllegalStateException] */
    fun startGame(gameId: String, mode: GameMode)

    /** Только в [RoomState.Playing], иначе [IllegalStateException] */
    fun sendEvent(payload: String)

    /** Sync-сообщения второго игрока */
    val syncEvents: Flow<RoomMessage.Sync>

    /** В [RoomState.Ready] и [RoomState.Playing], иначе [IllegalStateException] */
    fun sendSync(channel: String, payload: String)
    fun leave()
}

interface RoomFactory {
    fun host(me: Player, bindAddress: InetAddress): Room
    fun join(me: Player, host: InetAddress, port: Int): Room
}
