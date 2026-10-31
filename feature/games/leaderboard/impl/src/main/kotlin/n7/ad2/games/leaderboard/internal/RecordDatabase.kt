package n7.ad2.games.leaderboard.internal

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import n7.ad2.games.leaderboard.Record
import n7.ad2.games.room.GameMode

@Entity(tableName = "records", primaryKeys = ["playerId", "gameId", "mode"])
internal data class RecordEntity(
    val playerId: String,
    val playerName: String,
    val gameId: String,
    val mode: String,
    val score: Int,
    val achievedAt: Long,
)

internal fun RecordEntity.toRecord() = Record(playerId, playerName, gameId, GameMode.valueOf(mode), score, achievedAt)

internal fun Record.toEntity() = RecordEntity(playerId, playerName, gameId, mode.name, score, achievedAt)

@Dao
internal interface RecordDao {

    @Query("SELECT * FROM records WHERE playerId = :playerId AND gameId = :gameId AND mode = :mode")
    suspend fun get(playerId: String, gameId: String, mode: String): RecordEntity?

    @Upsert
    suspend fun upsert(entity: RecordEntity)

    @Query("SELECT * FROM records")
    suspend fun all(): List<RecordEntity>

    @Query("SELECT * FROM records WHERE gameId = :gameId AND mode = :mode ORDER BY score DESC, achievedAt ASC")
    fun observe(gameId: String, mode: String): Flow<List<RecordEntity>>
}

@Database(entities = [RecordEntity::class], version = 1, exportSchema = false)
internal abstract class LeaderboardDatabase : RoomDatabase() {
    abstract val recordDao: RecordDao
}
