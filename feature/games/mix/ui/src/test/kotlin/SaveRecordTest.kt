import com.google.common.truth.Truth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import n7.ad2.games.internal.leaderboard.saveRecord
import n7.ad2.games.leaderboard.LeaderboardRepository
import n7.ad2.games.leaderboard.LeaderboardSync
import n7.ad2.games.leaderboard.Record
import n7.ad2.games.leaderboard.isBetterThan
import n7.ad2.games.room.GameMode
import org.junit.Test

internal class SaveRecordTest {

    /** The table on the phone: one record per player, game and mode, the best first */
    private class Records(initial: List<Record>) : LeaderboardRepository {
        private val records = MutableStateFlow(initial)

        override fun observe(gameId: String, mode: GameMode): Flow<List<Record>> = records.map { all ->
            all.filter { it.gameId == gameId && it.mode == mode }.sortedWith(compareByDescending<Record> { it.score }.thenBy { it.achievedAt })
        }

        override suspend fun all(): List<Record> = records.value

        override suspend fun submit(record: Record): Boolean {
            val old = records.value.firstOrNull { it.playerId == record.playerId && it.gameId == record.gameId && it.mode == record.mode }
            if (old != null && !record.isBetterThan(old)) return false
            records.value = records.value - listOfNotNull(old).toSet() + record
            return true
        }

        override suspend fun merge(records: List<Record>): List<Record> = emptyList()
    }

    private fun record(id: String, score: Int, at: Long = 0) = Record(id, id, "invoker", GameMode.SOLO, score, at)

    @Test
    fun `a new record tells the record before it, the friends' table after it and who it passed`() = runBlocking<Unit> {
        val records = Records(listOf(record("galaxy", 2_640), record("pixel", 2_100), record("me", 1_980)))

        val saved = records.saveRecord(LeaderboardSync(records, this), record("me", 2_430, at = 5))

        Truth.assertThat(saved.isNew).isTrue()
        Truth.assertThat(saved.score).isEqualTo(2_430)
        Truth.assertThat(saved.previousBest).isEqualTo(1_980)
        Truth.assertThat(saved.friends.place).isEqualTo(2)
        Truth.assertThat(saved.passed.map { it.playerId }).containsExactly("pixel")
    }

    @Test
    fun `a game below the record keeps the record`() = runBlocking<Unit> {
        val records = Records(listOf(record("galaxy", 2_640), record("me", 1_980)))

        val saved = records.saveRecord(LeaderboardSync(records, this), record("me", 1_200, at = 5))

        Truth.assertThat(saved.isNew).isFalse()
        Truth.assertThat(saved.previousBest).isEqualTo(1_980)
        Truth.assertThat(saved.friends.mine?.score).isEqualTo(1_980)
        Truth.assertThat(saved.passed).isEmpty()
    }

    @Test
    fun `a game without points is no record`() = runBlocking<Unit> {
        val records = Records(listOf(record("galaxy", 300)))

        val saved = records.saveRecord(LeaderboardSync(records, this), record("me", 0))

        Truth.assertThat(saved.isNew).isFalse()
        Truth.assertThat(saved.previousBest).isNull()
        Truth.assertThat(saved.friends.mine).isNull()
        Truth.assertThat(records.all().map { it.playerId }).containsExactly("galaxy")
    }
}
