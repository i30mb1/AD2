import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import n7.ad2.games.leaderboard.LeaderboardSync
import n7.ad2.games.leaderboard.Record
import n7.ad2.games.leaderboard.inMemoryLeaderboardRepository
import n7.ad2.games.room.CloseReason
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Player
import n7.ad2.games.room.Room
import n7.ad2.games.room.RoomMessage
import n7.ad2.games.room.RoomState
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.CopyOnWriteArrayList

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
internal class LeaderboardSyncTest {

    private class FakeRoom : Room {
        override val me = Player("me", "Me")
        override val state = MutableStateFlow<RoomState>(RoomState.Connecting)
        override val events: Flow<RoomMessage.GameEvent> = emptyFlow()
        val incomingSync = MutableSharedFlow<RoomMessage.Sync>(extraBufferCapacity = 16)
        override val syncEvents: Flow<RoomMessage.Sync> = incomingSync
        val sentSync = CopyOnWriteArrayList<RoomMessage.Sync>()
        override fun sendSync(channel: String, payload: String) {
            sentSync += RoomMessage.Sync(channel, payload, me.id)
        }
        override fun startGame(gameId: String, mode: GameMode) = Unit
        override fun endGame() = Unit
        override fun sendEvent(payload: String) = Unit
        override fun leave() = Unit
    }

    private val host = Player("me", "Me")
    private val guest = Player("peer", "Peer")
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val repository = inMemoryLeaderboardRepository(ApplicationProvider.getApplicationContext())
    private val sync = LeaderboardSync(repository, scope)
    private val room = FakeRoom()
    private val recordsJson = ListSerializer(Record.serializer())

    @After
    fun tearDown() = scope.cancel()

    private fun record(player: String, score: Int) = Record(player, "Name $player", "spell", GameMode.SOLO, score, 100L)

    private fun sentRecords(): List<Record> = room.sentSync.filter { it.channel == "records" }.flatMap { Json.decodeFromString(recordsJson, it.payload) }

    private suspend fun eventually(check: suspend () -> Boolean) = withTimeout(3_000) {
        while (!check()) delay(20)
    }

    @Test
    fun `sends all records when room becomes Ready`() = runBlocking<Unit> {
        repository.submit(record("me", 5))
        sync.attach(room)

        room.state.value = RoomState.Ready(listOf(host, guest))

        eventually { sentRecords() == listOf(record("me", 5)) }
    }

    @Test
    fun `merges records received from peer`() = runBlocking<Unit> {
        sync.attach(room)
        room.state.value = RoomState.Ready(listOf(host, guest))
        delay(100)

        room.incomingSync.emit(RoomMessage.Sync("records", Json.encodeToString(recordsJson, listOf(record("peer", 7))), guest.id))

        eventually { repository.all() == listOf(record("peer", 7)) }
    }

    @Test
    fun `garbage payload is ignored`() = runBlocking<Unit> {
        sync.attach(room)
        room.state.value = RoomState.Ready(listOf(host, guest))
        delay(100)

        room.incomingSync.emit(RoomMessage.Sync("records", "not json", guest.id))
        room.incomingSync.emit(RoomMessage.Sync("records", Json.encodeToString(recordsJson, listOf(record("peer", 1))), guest.id))

        eventually { repository.all() == listOf(record("peer", 1)) }
    }

    @Test
    fun `submit while Playing broadcasts`() = runBlocking<Unit> {
        sync.attach(room)
        room.state.value = RoomState.Playing(listOf(host, guest), "spell", GameMode.SOLO, 1L)
        delay(100)

        Truth.assertThat(sync.submit(record("me", 9))).isTrue()

        eventually { record("me", 9) in sentRecords() }
    }

    @Test
    fun `submit when room closed only stores`() = runBlocking<Unit> {
        sync.attach(room)
        room.state.value = RoomState.Closed(CloseReason.HOST_LEFT)
        delay(100)

        sync.submit(record("me", 9))

        Truth.assertThat(repository.all()).containsExactly(record("me", 9))
        Truth.assertThat(room.sentSync).isEmpty()
    }

    @Test
    fun `a newcomer gets all records again, a game or someone leaving do not resend them`() = runBlocking<Unit> {
        repository.submit(record("me", 5))
        sync.attach(room)
        room.state.value = RoomState.Ready(listOf(host, guest))
        eventually { room.sentSync.size == 1 }

        val third = Player("third", "Third")
        room.state.value = RoomState.Ready(listOf(host, guest, third))
        eventually { room.sentSync.size == 2 }
        room.state.value = RoomState.Playing(listOf(host, guest, third), "show", GameMode.VERSUS, 1L)
        room.state.value = RoomState.Playing(listOf(host, third), "show", GameMode.VERSUS, 1L)
        room.state.value = RoomState.Ready(listOf(host, third))
        delay(200)

        Truth.assertThat(room.sentSync).hasSize(2)
        Truth.assertThat(sentRecords()).containsExactly(record("me", 5), record("me", 5))
    }

    @Test
    fun `alone again the host sends all records to the next one who comes`() = runBlocking<Unit> {
        sync.attach(room)
        room.state.value = RoomState.Ready(listOf(host, guest))
        eventually { room.sentSync.size == 1 }

        room.state.value = RoomState.Waiting(host, 5000)
        delay(100)
        room.state.value = RoomState.Ready(listOf(host, guest))

        eventually { room.sentSync.size == 2 }
    }

    @Test
    fun `50 records round-trip through sync`() = runBlocking<Unit> {
        val many = (1..50).map { record("p$it", it) }
        repository.merge(many)
        sync.attach(room)

        room.state.value = RoomState.Ready(listOf(host, guest))

        eventually { sentRecords().toSet() == many.toSet() }
    }
}
