import com.google.common.truth.Truth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import n7.ad2.games.room.CloseReason
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.MAX_PLAYERS
import n7.ad2.games.room.PROTOCOL_VERSION
import n7.ad2.games.room.Player
import n7.ad2.games.room.RoomCodec
import n7.ad2.games.room.RoomMessage
import n7.ad2.games.room.RoomState
import n7.ad2.games.room.internal.HostEvent
import n7.ad2.games.room.internal.HostRoom
import n7.ad2.games.room.internal.HostStatus
import n7.ad2.games.room.internal.HostTransport
import org.junit.Assert.assertThrows
import org.junit.Test

internal class HostRoomTest {

    private class FakeHostTransport : HostTransport {
        override val status = MutableStateFlow<HostStatus>(HostStatus.Starting)
        override val events = MutableSharedFlow<HostEvent>(extraBufferCapacity = 64)
        val sent = mutableListOf<Pair<Int, RoomMessage>>()
        val dropped = mutableListOf<Int>()
        var closed = false

        override fun send(client: Int, text: String) {
            sent += client to requireNotNull(RoomCodec.decode(text))
        }

        override fun drop(client: Int) {
            dropped += client
        }

        override fun close() {
            closed = true
        }

        fun join(client: Int, player: Player, version: Int = PROTOCOL_VERSION) {
            events.tryEmit(HostEvent.Joined(client))
            receive(client, RoomMessage.Hello(player, version))
        }

        fun receive(client: Int, message: RoomMessage) = events.tryEmit(HostEvent.Text(client, RoomCodec.encode(message)))

        fun left(client: Int) = events.tryEmit(HostEvent.Left(client))

        fun to(client: Int): List<RoomMessage> = sent.filter { it.first == client }.map { it.second }
    }

    private val host = Player("host", "Host")
    private val a = Player("a", "Anna")
    private val b = Player("b", "Boris")

    private fun TestScope.listening(transport: FakeHostTransport): HostRoom {
        val room = HostRoom(host, transport, backgroundScope, seedSource = { 42L })
        runCurrent()
        transport.status.value = HostStatus.Listening(5000)
        runCurrent()
        return room
    }

    private fun TestScope.withGuests(transport: FakeHostTransport, vararg guests: Player): HostRoom {
        val room = listening(transport)
        guests.forEachIndexed { index, guest -> transport.join(index + 1, guest) }
        runCurrent()
        return room
    }

    @Test
    fun `the host waits alone, guests join one by one and everyone gets the roster`() = runTest {
        val transport = FakeHostTransport()
        val room = listening(transport)
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Waiting(host, 5000))

        transport.join(1, a)
        runCurrent()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Ready(listOf(host, a)))
        Truth.assertThat(transport.to(1)).containsExactly(RoomMessage.Roster(listOf(host, a)))

        transport.join(2, b)
        runCurrent()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Ready(listOf(host, a, b)))
        Truth.assertThat(transport.to(1).last()).isEqualTo(RoomMessage.Roster(listOf(host, a, b)))
        Truth.assertThat(transport.to(2)).containsExactly(RoomMessage.Roster(listOf(host, a, b)))
    }

    @Test
    fun `a player too many is turned away as full`() = runTest {
        val transport = FakeHostTransport()
        val guests = (1 until MAX_PLAYERS).map { Player("p$it", "P$it") }
        val room = withGuests(transport, *guests.toTypedArray())
        Truth.assertThat((room.state.value as RoomState.Ready).players).hasSize(MAX_PLAYERS)

        transport.join(MAX_PLAYERS, Player("late", "Late"))
        runCurrent()

        Truth.assertThat(transport.to(MAX_PLAYERS)).containsExactly(RoomMessage.Reject(RoomMessage.Reject.FULL))
        Truth.assertThat(transport.dropped).containsExactly(MAX_PLAYERS)
        Truth.assertThat((room.state.value as RoomState.Ready).players).hasSize(MAX_PLAYERS)
    }

    @Test
    fun `a player of another version is turned away and nobody joins during a game`() = runTest {
        val transport = FakeHostTransport()
        val room = listening(transport)

        transport.join(1, a, version = PROTOCOL_VERSION + 1)
        runCurrent()
        Truth.assertThat(transport.to(1)).containsExactly(RoomMessage.Reject(RoomMessage.Reject.VERSION))
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Waiting(host, 5000))

        transport.join(2, a)
        runCurrent()
        room.startGame("show", GameMode.VERSUS)
        transport.join(3, b)
        runCurrent()
        Truth.assertThat(transport.to(3)).containsExactly(RoomMessage.Reject(RoomMessage.Reject.BUSY))
        Truth.assertThat(transport.dropped).containsExactly(1, 3)
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Playing(listOf(host, a), "show", GameMode.VERSUS, 42L))
    }

    @Test
    fun `the host starts a game with one seed for everyone and ends it back to ready`() = runTest {
        val transport = FakeHostTransport()
        val room = withGuests(transport, a, b)

        room.startGame("show", GameMode.COOP)
        runCurrent()

        val start = RoomMessage.StartGame("show", GameMode.COOP, 42L)
        Truth.assertThat(transport.to(1).last()).isEqualTo(start)
        Truth.assertThat(transport.to(2).last()).isEqualTo(start)
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Playing(listOf(host, a, b), "show", GameMode.COOP, 42L))

        room.endGame()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Ready(listOf(host, a, b)))
    }

    @Test
    fun `an event of a guest comes out at the host and goes on to the other guests`() = runTest {
        val transport = FakeHostTransport()
        val room = withGuests(transport, a, b)
        room.startGame("invoker", GameMode.VERSUS)
        val events = mutableListOf<RoomMessage.GameEvent>()
        backgroundScope.launch { room.events.toList(events) }
        runCurrent()

        // the id comes from the connection, not from the message
        transport.receive(1, RoomMessage.GameEvent("someone", "x"))
        runCurrent()
        Truth.assertThat(events).containsExactly(RoomMessage.GameEvent(a.id, "x"))
        Truth.assertThat(transport.to(2).last()).isEqualTo(RoomMessage.GameEvent(a.id, "x"))
        Truth.assertThat(transport.to(1)).doesNotContain(RoomMessage.GameEvent(a.id, "x"))

        room.sendEvent("y")
        runCurrent()
        Truth.assertThat(transport.to(1).last()).isEqualTo(RoomMessage.GameEvent(host.id, "y"))
        Truth.assertThat(transport.to(2).last()).isEqualTo(RoomMessage.GameEvent(host.id, "y"))
        Truth.assertThat(events).hasSize(1)
    }

    @Test
    fun `syncs go around the same way with the sender's id`() = runTest {
        val transport = FakeHostTransport()
        val room = withGuests(transport, a, b)
        val syncs = mutableListOf<RoomMessage.Sync>()
        backgroundScope.launch { room.syncEvents.toList(syncs) }
        runCurrent()

        transport.receive(2, RoomMessage.Sync("lobby", "r", ""))
        runCurrent()
        room.sendSync("lobby", "g")
        runCurrent()

        Truth.assertThat(syncs).containsExactly(RoomMessage.Sync("lobby", "r", b.id))
        Truth.assertThat(transport.to(1)).containsAtLeast(RoomMessage.Sync("lobby", "r", b.id), RoomMessage.Sync("lobby", "g", host.id)).inOrder()
        Truth.assertThat(transport.to(2)).doesNotContain(RoomMessage.Sync("lobby", "r", b.id))
        Truth.assertThat(transport.to(2).last()).isEqualTo(RoomMessage.Sync("lobby", "g", host.id))
    }

    @Test
    fun `alone the host sends syncs to nobody`() = runTest {
        val transport = FakeHostTransport()
        val room = listening(transport)

        room.sendSync("lobby", "g")
        runCurrent()

        Truth.assertThat(transport.sent).isEmpty()
    }

    @Test
    fun `a guest leaving shrinks the roster, the last one puts the host back to waiting`() = runTest {
        val transport = FakeHostTransport()
        val room = withGuests(transport, a, b)

        transport.receive(1, RoomMessage.Leave(a.id))
        runCurrent()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Ready(listOf(host, b)))
        Truth.assertThat(transport.to(2).last()).isEqualTo(RoomMessage.Roster(listOf(host, b)))
        Truth.assertThat(transport.dropped).containsExactly(1)

        transport.left(2)
        runCurrent()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Waiting(host, 5000))
    }

    @Test
    fun `a guest leaving during a game keeps the game for the others`() = runTest {
        val transport = FakeHostTransport()
        val room = withGuests(transport, a, b)
        room.startGame("show", GameMode.VERSUS)

        transport.left(1)
        runCurrent()

        Truth.assertThat(room.state.value).isEqualTo(RoomState.Playing(listOf(host, b), "show", GameMode.VERSUS, 42L))
        Truth.assertThat(transport.to(2).last()).isEqualTo(RoomMessage.Roster(listOf(host, b)))
        room.endGame()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Ready(listOf(host, b)))
    }

    @Test
    fun `the host leaving tells everyone and closes`() = runTest {
        val transport = FakeHostTransport()
        val room = withGuests(transport, a, b)

        room.leave()
        runCurrent()

        Truth.assertThat(transport.to(1).last()).isEqualTo(RoomMessage.Leave(host.id))
        Truth.assertThat(transport.to(2).last()).isEqualTo(RoomMessage.Leave(host.id))
        Truth.assertThat(transport.closed).isTrue()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Closed(CloseReason.LEFT))
    }

    @Test
    fun `calls in a wrong state throw and send nothing`() = runTest {
        val transport = FakeHostTransport()
        val room = listening(transport)

        assertThrows(IllegalStateException::class.java) { room.startGame("show", GameMode.VERSUS) }
        transport.join(1, a)
        runCurrent()
        val sentBefore = transport.sent.toList()
        assertThrows(IllegalStateException::class.java) { room.sendEvent("x") }
        Truth.assertThat(transport.sent).isEqualTo(sentBefore)
    }

    @Test
    fun `strangers and garbage are ignored`() = runTest {
        val transport = FakeHostTransport()
        val room = withGuests(transport, a)
        val syncs = mutableListOf<RoomMessage.Sync>()
        backgroundScope.launch { room.syncEvents.toList(syncs) }
        runCurrent()

        // connected but never said hello
        transport.events.tryEmit(HostEvent.Joined(9))
        transport.receive(9, RoomMessage.Sync("lobby", "x", "a"))
        transport.events.tryEmit(HostEvent.Text(1, "not json"))
        runCurrent()

        Truth.assertThat(syncs).isEmpty()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Ready(listOf(host, a)))
    }

    @Test
    fun `the server failing closes the room`() = runTest {
        val transport = FakeHostTransport()
        val room = listening(transport)

        transport.status.value = HostStatus.Stopped(failed = true)
        runCurrent()

        Truth.assertThat(room.state.value).isEqualTo(RoomState.Closed(CloseReason.CONNECTION_FAILED))
    }
}
