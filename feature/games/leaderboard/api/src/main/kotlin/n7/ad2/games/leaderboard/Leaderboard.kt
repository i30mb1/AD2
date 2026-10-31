package n7.ad2.games.leaderboard

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Player

/** Лучший результат игрока в игре и режиме; таблица хранит по одной записи на (playerId, gameId, mode) */
@Serializable
data class Record(val playerId: String, val playerName: String, val gameId: String, val mode: GameMode, val score: Int, val achievedAt: Long)

/** Больше очков, а при равенстве - раньше достигнут: так обе таблицы сходятся к одной записи */
fun Record.isBetterThan(other: Record): Boolean = score > other.score || (score == other.score && achievedAt < other.achievedAt)

interface LeaderboardRepository {
    /** Записи игры и режима, по убыванию очков, при равенстве - раньше выше */
    fun observe(gameId: String, mode: GameMode): Flow<List<Record>>
    suspend fun all(): List<Record>

    /** true, если запись стала лучшей для игрока */
    suspend fun submit(record: Record): Boolean

    /** Записи второго игрока; возвращает только те, что улучшили таблицу */
    suspend fun merge(records: List<Record>): List<Record>
}

interface LocalPlayerProvider {
    fun localPlayer(): Player
}
