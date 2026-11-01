package n7.ad2.games.internal.party

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import n7.ad2.games.room.Room
import n7.ad2.games.room.RoomMessage
import n7.ad2.games.room.RoomState

/**
 * Служебные сообщения экрана игры поверх sync: комната живет дольше игры, поэтому выход из игры - не выход из комнаты
 */
internal object GameChannel {
    private const val CHANNEL = "game"
    private const val QUIT = "quit"

    /** Ушли с экрана посреди игры: остальные доиграют без меня */
    fun quit(room: Room) {
        runCatching { room.sendSync(CHANNEL, QUIT) }
    }

    /**
     * Кто из [others] выбыл из игры: ушел с экрана игры, пропал из комнаты или, если комната закрылась, все сразу.
     * Каждый - один раз.
     */
    fun departures(room: Room, others: Set<String>): Flow<String> {
        val quits = room.syncEvents.filter(::isQuit).map { setOf(it.playerId) }
        val gone = room.state.map { state ->
            when (state) {
                is RoomState.Ready -> others - state.players.map { it.id }.toSet()
                is RoomState.Playing -> others - state.players.map { it.id }.toSet()
                is RoomState.Waiting, is RoomState.Closed -> others
                RoomState.Connecting -> emptySet()
            }
        }
        return flow {
            val left = mutableSetOf<String>()
            merge(quits, gone).collect { ids -> ids.filter { it in others && left.add(it) }.forEach { emit(it) } }
        }
    }

    private fun isQuit(message: RoomMessage.Sync): Boolean = message.channel == CHANNEL && message.payload == QUIT
}
