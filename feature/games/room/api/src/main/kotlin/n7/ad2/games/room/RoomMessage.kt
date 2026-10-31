package n7.ad2.games.room

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Увеличивать при несовместимых изменениях протокола: хост отклонит гостя с другой версией */
const val PROTOCOL_VERSION = 1

@Serializable
data class Player(val id: String, val name: String)

@Serializable
enum class GameMode { SOLO, VERSUS, COOP }

/**
 * Сообщения комнаты на двоих. Хост: [Welcome], [Reject], [StartGame]. Гость: [Hello]. Оба: [GameEvent], [Leave].
 * [GameEvent.payload] - данные конкретной игры, комната их не разбирает.
 */
@Serializable
sealed interface RoomMessage {

    @Serializable
    @SerialName("hello")
    data class Hello(val player: Player, val protocolVersion: Int) : RoomMessage

    @Serializable
    @SerialName("welcome")
    data class Welcome(val host: Player, val guest: Player) : RoomMessage

    @Serializable
    @SerialName("reject")
    data class Reject(val reason: String) : RoomMessage

    /** [seed] одинаковый у обоих игроков, чтобы раунды генерировались одинаково */
    @Serializable
    @SerialName("start")
    data class StartGame(val gameId: String, val mode: GameMode, val seed: Long) : RoomMessage

    @Serializable
    @SerialName("event")
    data class GameEvent(val playerId: String, val payload: String) : RoomMessage

    @Serializable
    @SerialName("leave")
    data class Leave(val playerId: String) : RoomMessage

    /** Данные вне игры (например таблица рекордов), ходят в Ready и Playing; [channel] разделяет получателей */
    @Serializable
    @SerialName("sync")
    data class Sync(val channel: String, val payload: String) : RoomMessage
}
