package n7.ad2.games.leaderboard

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import n7.ad2.games.leaderboard.internal.LeaderboardDatabase
import n7.ad2.games.leaderboard.internal.toEntity
import n7.ad2.games.leaderboard.internal.toRecord
import n7.ad2.games.room.GameMode

fun LeaderboardRepository(context: Context): LeaderboardRepository =
    RoomLeaderboardRepository(Room.databaseBuilder(context, LeaderboardDatabase::class.java, "games_leaderboard.db").build())

internal fun inMemoryLeaderboardRepository(context: Context): LeaderboardRepository =
    RoomLeaderboardRepository(Room.inMemoryDatabaseBuilder(context, LeaderboardDatabase::class.java).allowMainThreadQueries().build())

internal class RoomLeaderboardRepository(private val database: LeaderboardDatabase) : LeaderboardRepository {

    private val dao = database.recordDao

    override fun observe(gameId: String, mode: GameMode): Flow<List<Record>> = dao.observe(gameId, mode.name).map { list -> list.map { it.toRecord() } }

    override suspend fun all(): List<Record> = dao.all().map { it.toRecord() }

    override suspend fun submit(record: Record): Boolean = database.withTransaction { storeIfBetter(record) }

    override suspend fun merge(records: List<Record>): List<Record> = database.withTransaction { records.filter { storeIfBetter(it) } }

    private suspend fun storeIfBetter(record: Record): Boolean {
        val stored = dao.get(record.playerId, record.gameId, record.mode.name)?.toRecord()
        if (stored != null && !record.isBetterThan(stored)) return false
        dao.upsert(record.toEntity())
        return true
    }
}
