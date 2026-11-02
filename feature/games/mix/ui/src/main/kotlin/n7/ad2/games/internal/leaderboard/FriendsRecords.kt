package n7.ad2.games.internal.leaderboard

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import n7.ad2.games.leaderboard.LeaderboardRepository
import n7.ad2.games.leaderboard.LeaderboardSync
import n7.ad2.games.leaderboard.Record

/**
 * The records of a game and mode on this phone, the best first: mine and of the players met over the network.
 * A game card tells my place among them, the end of a solo game shows the [table].
 */
@Immutable
internal data class FriendsRecords(val records: List<Record>, val meId: String) {

    private val myIndex = records.indexOfFirst { it.playerId == meId }

    val mine: Record? get() = records.getOrNull(myIndex)

    /** The players met over the network who have played the game */
    val others: List<Record> get() = records.filter { it.playerId != meId }

    /** 1 - the best of all, null - no record of mine */
    val place: Int? get() = if (myIndex < 0) null else myIndex + 1

    val leader: Record? get() = records.firstOrNull()

    /** The one right above me, null - I am the first or have no record */
    val above: Record? get() = if (myIndex > 0) records[myIndex - 1] else null

    /** The one right below me */
    val below: Record? get() = if (myIndex < 0) null else records.getOrNull(myIndex + 1)

    /** How many points the one above me has more, null - nobody above or the same score got earlier */
    val behind: Int? get() = above?.let { above -> (above.score - records[myIndex].score).takeIf { it > 0 } }

    /** The rows of an end screen with their places: the first three and me below them */
    val table: List<IndexedValue<Record>> get() = records.withIndex().filter { it.index < TOP || it.index == myIndex }

    /** Who my record passed: above me in [before] and below me now; my first record passes nobody */
    fun passed(before: FriendsRecords): List<Record> {
        if (before.myIndex < 0 || myIndex < 0) return emptyList()
        val wereAbove = before.records.take(before.myIndex).map { it.playerId }.toSet()
        return records.drop(myIndex + 1).filter { it.playerId in wereAbove }
    }

    private companion object {
        const val TOP = 3
    }
}

/** A finished game saved: [previousBest] - my record before it, null - none; [friends] - the table after it; [passed] - who it passed */
@Immutable
internal data class SavedRecord(val score: Int, val previousBest: Int?, val isNew: Boolean, val friends: FriendsRecords, val passed: List<Record>)

/** Saves the score of a finished game as my record when it is better; a game without points is no record */
internal suspend fun LeaderboardRepository.saveRecord(sync: LeaderboardSync, record: Record): SavedRecord {
    val before = friendsRecords(record)
    val isNew = record.score > 0 && sync.submit(record)
    val after = friendsRecords(record)
    return SavedRecord(record.score, before.mine?.score, isNew, after, after.passed(before))
}

private suspend fun LeaderboardRepository.friendsRecords(record: Record) =
    FriendsRecords(observe(record.gameId, record.mode).catch { emit(emptyList()) }.first(), record.playerId)
