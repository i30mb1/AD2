package n7.ad2.games.room

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Увеличивать при несовместимых изменениях протокола: хост отклонит гостя с другой версией */
// 2: комната переживает игру (сессия на несколько игр), выбор игры идет sync-сообщениями
// 3: до 6 игроков: хост пересылает сообщения гостей остальным, состав комнаты - Roster
const val PROTOCOL_VERSION = 3

/** Хост и до пяти гостей */
const val MAX_PLAYERS = 6

@Serializable
data class Player(val id: String, val name: String)

@Serializable
enum class GameMode { SOLO, VERSUS, COOP }

/**
 * Сообщения комнаты. Гость говорит [Hello] хосту, хост отвечает [Roster] или [Reject] и рассылает [Roster] всем при
 * каждом входе и выходе, [StartGame] запускает игру у всех. [GameEvent] и [Sync] гостя идут хосту, хост пересылает их
 * остальным гостям. [GameEvent.payload] и [Sync.payload] - данные игры и экранов, комната их не разбирает.
 */
@Serializable
sealed interface RoomMessage {

    @Serializable
    @SerialName("hello")
    data class Hello(val player: Player, val protocolVersion: Int) : RoomMessage

    /** Все в комнате, хост первый, гости - в порядке входа; первый [Roster] со мной - я в комнате */
    @Serializable
    @SerialName("roster")
    data class Roster(val players: List<Player>) : RoomMessage

    @Serializable
    @SerialName("reject")
    data class Reject(val reason: String) : RoomMessage {
        companion object {
            const val VERSION = "version"
            const val FULL = "full"
            const val BUSY = "busy"
        }
    }

    /** [seed] одинаковый у всех игроков, чтобы раунды генерировались одинаково */
    @Serializable
    @SerialName("start")
    data class StartGame(val gameId: String, val mode: GameMode, val seed: Long) : RoomMessage

    @Serializable
    @SerialName("event")
    data class GameEvent(val playerId: String, val payload: String) : RoomMessage

    @Serializable
    @SerialName("leave")
    data class Leave(val playerId: String) : RoomMessage

    /** Данные вне игры (выбор игры, таблица рекордов), ходят в Ready и Playing; [channel] разделяет получателей, [playerId] - отправитель */
    @Serializable
    @SerialName("sync")
    data class Sync(val channel: String, val payload: String, val playerId: String) : RoomMessage
}
