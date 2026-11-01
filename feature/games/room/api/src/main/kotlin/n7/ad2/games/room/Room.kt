package n7.ad2.games.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.net.InetAddress

sealed interface RoomState {
    data object Connecting : RoomState

    /** Хост один, гости входят на [port] */
    data class Waiting(val host: Player, val port: Int) : RoomState

    /** [players] - хост первый, гости в порядке входа */
    data class Ready(val players: List<Player>) : RoomState {
        val host: Player get() = players.first()
    }

    data class Playing(val players: List<Player>, val gameId: String, val mode: GameMode, val seed: Long) : RoomState {
        val host: Player get() = players.first()

        fun others(myId: String): List<Player> = players.filter { it.id != myId }
    }

    data class Closed(val reason: CloseReason) : RoomState
}

/** [HOST_LEFT] - хост вышел или связь с ним пропала, [FULL] - в комнате уже [MAX_PLAYERS], [BUSY] - там идет игра */
enum class CloseReason { LEFT, HOST_LEFT, CONNECTION_FAILED, VERSION_MISMATCH, FULL, BUSY }

/**
 * Комната до [MAX_PLAYERS] игроков: хост и гости. Комната переживает игры: после игры все снова в [RoomState.Ready].
 * Гость, ушедший во время игры, пропадает из [RoomState.Playing.players], хост, ушедший из комнаты, закрывает ее у всех.
 */
interface Room {
    val me: Player
    val state: StateFlow<RoomState>

    /** События остальных игроков */
    val events: Flow<RoomMessage.GameEvent>

    /** Только хост в [RoomState.Ready], иначе [IllegalStateException] */
    fun startGame(gameId: String, mode: GameMode)

    /** Игра закончилась, комната остается: [RoomState.Playing] -> [RoomState.Ready], в других состояниях ничего не делает */
    fun endGame()

    /** Только в [RoomState.Playing], иначе [IllegalStateException] */
    fun sendEvent(payload: String)

    /** Sync-сообщения остальных игроков */
    val syncEvents: Flow<RoomMessage.Sync>

    /** Всем остальным; хост один - никому; гость - только в [RoomState.Ready] и [RoomState.Playing], иначе [IllegalStateException] */
    fun sendSync(channel: String, payload: String)
    fun leave()
}

interface RoomFactory {
    fun host(me: Player, bindAddress: InetAddress): Room
    fun join(me: Player, host: InetAddress, port: Int): Room
}
