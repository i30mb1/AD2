import com.google.common.truth.Truth
import n7.ad2.games.internal.leaderboard.FriendsRecords
import n7.ad2.games.leaderboard.Record
import n7.ad2.games.room.GameMode
import org.junit.Test

internal class FriendsRecordsTest {

    private fun record(id: String, score: Int, at: Long = 0) = Record(id, id, "invoker", GameMode.SOLO, score, at)

    @Test
    fun `my place among the friends and who is right above me`() {
        val records = FriendsRecords(listOf(record("galaxy", 2_640), record("me", 2_430), record("pixel", 2_100), record("redmi", 900)), meId = "me")

        Truth.assertThat(records.place).isEqualTo(2)
        Truth.assertThat(records.mine?.score).isEqualTo(2_430)
        Truth.assertThat(records.above?.playerId).isEqualTo("galaxy")
        Truth.assertThat(records.behind).isEqualTo(210)
        Truth.assertThat(records.others.map { it.playerId }).containsExactly("galaxy", "pixel", "redmi").inOrder()
    }

    @Test
    fun `the first names the one who follows, without a record of mine the leader is named`() {
        val first = FriendsRecords(listOf(record("me", 2_450), record("galaxy", 2_330)), meId = "me")

        Truth.assertThat(first.place).isEqualTo(1)
        Truth.assertThat(first.above).isNull()
        Truth.assertThat(first.below?.playerId).isEqualTo("galaxy")
        Truth.assertThat(first.behind).isNull()

        val notPlayed = FriendsRecords(listOf(record("galaxy", 41), record("pixel", 30)), meId = "me")

        Truth.assertThat(notPlayed.place).isNull()
        Truth.assertThat(notPlayed.mine).isNull()
        Truth.assertThat(notPlayed.leader?.playerId).isEqualTo("galaxy")
    }

    @Test
    fun `alone there is no friend to compare with`() {
        Truth.assertThat(FriendsRecords(listOf(record("me", 500)), meId = "me").others).isEmpty()
        Truth.assertThat(FriendsRecords(emptyList(), meId = "me").others).isEmpty()
    }

    @Test
    fun `the end table keeps the first three and me below them`() {
        val records = FriendsRecords((1..6).map { record("p$it", 1_000 - it * 100) } + record("me", 50), meId = "me")

        Truth.assertThat(records.table.map { it.index + 1 to it.value.playerId })
            .containsExactly(1 to "p1", 2 to "p2", 3 to "p3", 7 to "me").inOrder()
    }

    @Test
    fun `a new record names the friends it passed and not the ones below me before`() {
        val before = FriendsRecords(listOf(record("galaxy", 2_640), record("pixel", 2_100), record("me", 1_980), record("redmi", 900)), meId = "me")
        val after = FriendsRecords(listOf(record("galaxy", 2_640), record("me", 2_430), record("pixel", 2_100), record("redmi", 900)), meId = "me")

        Truth.assertThat(after.passed(before).map { it.playerId }).containsExactly("pixel")
    }

    @Test
    fun `the first record of mine passes nobody`() {
        val before = FriendsRecords(listOf(record("galaxy", 2_640), record("pixel", 90)), meId = "me")
        val after = FriendsRecords(listOf(record("galaxy", 2_640), record("me", 100), record("pixel", 90)), meId = "me")

        Truth.assertThat(after.passed(before)).isEmpty()
    }

    @Test
    fun `the same score as the one above is no gap to tell`() {
        val records = FriendsRecords(listOf(record("galaxy", 500, at = 1), record("me", 500, at = 2)), meId = "me")

        Truth.assertThat(records.place).isEqualTo(2)
        Truth.assertThat(records.behind).isNull()
    }
}
