package n7.ad2.games.internal.party

import n7.ad2.games.room.Room
import n7.ad2.games.room.RoomMessage

/**
 * Служебные сообщения экрана игры поверх sync: комната живет дольше игры, поэтому выход из игры - не выход из комнаты
 */
internal object GameChannel {
    private const val CHANNEL = "game"
    private const val QUIT = "quit"

    /** Ушли с экрана посреди игры: второй игрок закончит ее как «соперник вышел» */
    fun quit(room: Room) {
        runCatching { room.sendSync(CHANNEL, QUIT) }
    }

    fun isQuit(message: RoomMessage.Sync): Boolean = message.channel == CHANNEL && message.payload == QUIT
}
