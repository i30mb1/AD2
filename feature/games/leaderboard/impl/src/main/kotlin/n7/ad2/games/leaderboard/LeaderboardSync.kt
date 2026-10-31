package n7.ad2.games.leaderboard

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import n7.ad2.games.room.Room
import n7.ad2.games.room.RoomState

/**
 * Синхронизирует таблицу рекордов с игроком в комнате: при встрече обмен всеми записями,
 * дальше каждый новый рекорд сразу уходит второму игроку.
 */
class LeaderboardSync(private val repository: LeaderboardRepository, private val scope: CoroutineScope) {

    private val json = Json { ignoreUnknownKeys = true }
    private val recordsSerializer = ListSerializer(Record.serializer())

    @Volatile private var room: Room? = null
    private var job: Job? = null

    fun attach(room: Room) {
        job?.cancel()
        this.room = room
        job = scope.launch {
            launch {
                room.state.first { it.isTogether() }
                send(room, repository.all())
            }
            room.syncEvents
                .filter { it.channel == CHANNEL }
                .collect { sync -> decode(sync.payload)?.let { repository.merge(it) } }
        }
    }

    /** Сохраняет результат; если это новый рекорд и второй игрок на связи - отправляет ему */
    suspend fun submit(record: Record): Boolean {
        val improved = repository.submit(record)
        val current = room
        if (improved && current != null && current.state.value.isTogether()) send(current, listOf(record))
        return improved
    }

    private fun send(room: Room, records: List<Record>) {
        // комната могла закрыться между проверкой и отправкой - рекорды уже сохранены, синхронизируются при следующей встрече
        runCatching { room.sendSync(CHANNEL, json.encodeToString(recordsSerializer, records)) }
    }

    private fun decode(payload: String): List<Record>? = try {
        json.decodeFromString(recordsSerializer, payload)
    } catch (e: SerializationException) {
        null
    } catch (e: IllegalArgumentException) {
        null
    }

    private fun RoomState.isTogether() = this is RoomState.Ready || this is RoomState.Playing

    private companion object {
        const val CHANNEL = "records"
    }
}
