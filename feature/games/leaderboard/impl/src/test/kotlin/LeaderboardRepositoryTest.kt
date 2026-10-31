import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import n7.ad2.games.leaderboard.LeaderboardRepository
import n7.ad2.games.leaderboard.Record
import n7.ad2.games.leaderboard.inMemoryLeaderboardRepository
import n7.ad2.games.room.GameMode
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
internal class LeaderboardRepositoryTest {

    private val repository: LeaderboardRepository = inMemoryLeaderboardRepository(ApplicationProvider.getApplicationContext())

    private fun record(player: String = "p1", score: Int, at: Long = 100L, game: String = "spell", mode: GameMode = GameMode.SOLO) =
        Record(player, "Name $player", game, mode, score, at)

    @Test
    fun `submit stores first record`() = runBlocking<Unit> {
        Truth.assertThat(repository.submit(record(score = 5))).isTrue()
        Truth.assertThat(repository.all()).containsExactly(record(score = 5))
    }

    @Test
    fun `worse score is ignored`() = runBlocking<Unit> {
        repository.submit(record(score = 5))

        Truth.assertThat(repository.submit(record(score = 3, at = 200))).isFalse()
        Truth.assertThat(repository.all()).containsExactly(record(score = 5))
    }

    @Test
    fun `better score replaces`() = runBlocking<Unit> {
        repository.submit(record(score = 5))

        Truth.assertThat(repository.submit(record(score = 8, at = 200))).isTrue()
        Truth.assertThat(repository.all()).containsExactly(record(score = 8, at = 200))
    }

    @Test
    fun `tie keeps earlier achievedAt`() = runBlocking<Unit> {
        repository.submit(record(score = 5, at = 200))

        Truth.assertThat(repository.submit(record(score = 5, at = 100))).isTrue()
        Truth.assertThat(repository.submit(record(score = 5, at = 300))).isFalse()
        Truth.assertThat(repository.all()).containsExactly(record(score = 5, at = 100))
    }

    @Test
    fun `merge returns only improvements`() = runBlocking<Unit> {
        repository.submit(record(player = "p1", score = 5))

        val improved = repository.merge(listOf(record(player = "p1", score = 4), record(player = "p2", score = 2)))

        Truth.assertThat(improved).containsExactly(record(player = "p2", score = 2))
        Truth.assertThat(repository.all()).containsExactly(record(player = "p1", score = 5), record(player = "p2", score = 2))
    }

    @Test
    fun `merge keeps the better local record`() = runBlocking<Unit> {
        repository.submit(record(player = "me", score = 10, at = 500))

        repository.merge(listOf(record(player = "me", score = 7, at = 100)))

        Truth.assertThat(repository.all()).containsExactly(record(player = "me", score = 10, at = 500))
    }

    @Test
    fun `observe sorts by score then time and filters by game and mode`() = runBlocking<Unit> {
        repository.merge(
            listOf(
                record(player = "a", score = 3, at = 10),
                record(player = "b", score = 9, at = 20),
                record(player = "c", score = 9, at = 5),
                record(player = "d", score = 100, mode = GameMode.VERSUS),
                record(player = "e", score = 100, game = "creep"),
            ),
        )

        val board = repository.observe("spell", GameMode.SOLO).first()

        Truth.assertThat(board.map { it.playerId }).containsExactly("c", "b", "a").inOrder()
    }
}
