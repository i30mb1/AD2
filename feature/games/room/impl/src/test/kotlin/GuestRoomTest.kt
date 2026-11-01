import com.google.common.truth.Truth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import n7.ad2.games.room.CloseReason
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.PROTOCOL_VERSION
import n7.ad2.games.room.Player
import n7.ad2.games.room.RoomCodec
import n7.ad2.games.room.RoomMessage
import n7.ad2.games.room.RoomState
import n7.ad2.games.room.internal.GuestRoom
import n7.ad2.games.room.internal.Transport
import n7.ad2.games.room.internal.TransportEvent
import org.junit.Assert.assertThrows
import org.junit.Test

internal class GuestRoomTest {

    private class FakeTransport : Transport {
        override val events = MutableSharedFlow<TransportEvent>(extraBufferCapacity = 16)
        val sent = mutableListOf<RoomMessage>()
        var closed = false

        override fun send(text: String) {
            sent += requireNotNull(RoomCodec.decode(text))
        }

        override fun close() {
            closed = true
        }

        fun receive(message: RoomMessage) = events.tryEmit(TransportEvent.Text(RoomCodec.encode(message)))

        fun connected() = events.tryEmit(TransportEvent.Connected)

        fun broken(failed: Boolean) = events.tryEmit(TransportEvent.Closed(failed))
    }

    private val host = Player("host", "Host")
    private val me = Player("me", "Me")
    private val third = Player("third", "Third")

    private fun TestScope.connected(transport: FakeTransport): GuestRoom {
        val room = GuestRoom(me, transport, backgroundScope)
        runCurrent()
        transport.connected()
        runCurrent()
        return room
    }

    private fun TestScope.inRoom(transport: FakeTransport, vararg players: Player = arrayOf(host, me)): GuestRoom {
        val room = connected(transport)
        transport.receive(RoomMessage.Roster(players.toList()))
        runCurrent()
        return room
    }

    @Test
    fun `the guest says hello on connect and is in once the roster has it`() = runTest {
        val transport = FakeTransport()
        val room = connected(transport)
        Truth.assertThat(transport.sent).containsExactly(RoomMessage.Hello(me, PROTOCOL_VERSION))

        transport.receive(RoomMessage.Roster(listOf(host, third)))
        runCurrent()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Connecting)

        transport.receive(RoomMessage.Roster(listOf(host, me)))
        runCurrent()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Ready(listOf(host, me)))

        transport.receive(RoomMessage.Roster(listOf(host, me, third)))
        runCurrent()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Ready(listOf(host, me, third)))
    }

    @Test
    fun `the guest follows the host into a game, keeps the roster there and ends it back to ready`() = runTest {
        val transport = FakeTransport()
        val room = inRoom(transport, host, me, third)

        transport.receive(RoomMessage.StartGame("show", GameMode.COOP, 7L))
        runCurrent()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Playing(listOf(host, me, third), "show", GameMode.COOP, 7L))

        transport.receive(RoomMessage.Roster(listOf(host, me)))
        runCurrent()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Playing(listOf(host, me), "show", GameMode.COOP, 7L))

        room.endGame()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Ready(listOf(host, me)))
    }

    @Test
    fun `a guest still in the previous game takes the next start`() = runTest {
        val transport = FakeTransport()
        val room = inRoom(transport)
        transport.receive(RoomMessage.StartGame("show", GameMode.VERSUS, 1L))
        runCurrent()

        transport.receive(RoomMessage.StartGame("invoker", GameMode.COOP, 2L))
        runCurrent()

        Truth.assertThat(room.state.value).isEqualTo(RoomState.Playing(listOf(host, me), "invoker", GameMode.COOP, 2L))
    }

    @Test
    fun `a reject closes the room with its reason`() = runTest {
        val reasons = mapOf(
            RoomMessage.Reject.VERSION to CloseReason.VERSION_MISMATCH,
            RoomMessage.Reject.FULL to CloseReason.FULL,
            RoomMessage.Reject.BUSY to CloseReason.BUSY,
        )
        reasons.forEach { (reject, reason) ->
            val transport = FakeTransport()
            val room = connected(transport)

            transport.receive(RoomMessage.Reject(reject))
            runCurrent()

            Truth.assertThat(room.state.value).isEqualTo(RoomState.Closed(reason))
            Truth.assertThat(transport.closed).isTrue()
        }
    }

    @Test
    fun `events and syncs of the others come out, own ones do not`() = runTest {
        val transport = FakeTransport()
        val room = inRoom(transport, host, me, third)
        val events = mutableListOf<RoomMessage.GameEvent>()
        val syncs = mutableListOf<RoomMessage.Sync>()
        backgroundScope.launch { room.events.toList(events) }
        backgroundScope.launch { room.syncEvents.toList(syncs) }
        transport.receive(RoomMessage.StartGame("invoker", GameMode.VERSUS, 1L))
        runCurrent()

        transport.receive(RoomMessage.GameEvent(third.id, "a"))
        transport.receive(RoomMessage.GameEvent(me.id, "echo"))
        transport.receive(RoomMessage.Sync("lobby", "x", host.id))
        transport.receive(RoomMessage.Sync("lobby", "echo", me.id))
        room.sendEvent("b")
        room.sendSync("lobby", "r")
        runCurrent()

        Truth.assertThat(events).containsExactly(RoomMessage.GameEvent(third.id, "a"))
        Truth.assertThat(syncs).containsExactly(RoomMessage.Sync("lobby", "x", host.id))
        Truth.assertThat(transport.sent.takeLast(2)).containsExactly(RoomMessage.GameEvent(me.id, "b"), RoomMessage.Sync("lobby", "r", me.id)).inOrder()
    }

    @Test
    fun `the host leaving or the connection breaking ends the room as HOST_LEFT`() = runTest {
        val leaving = FakeTransport()
        val room = inRoom(leaving, host, me, third)
        leaving.receive(RoomMessage.Leave(third.id))
        runCurrent()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Ready(listOf(host, me, third)))
        leaving.receive(RoomMessage.Leave(host.id))
        runCurrent()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Closed(CloseReason.HOST_LEFT))
        Truth.assertThat(leaving.closed).isTrue()

        val breaking = FakeTransport()
        val broken = inRoom(breaking)
        breaking.broken(failed = false)
        runCurrent()
        Truth.assertThat(broken.state.value).isEqualTo(RoomState.Closed(CloseReason.HOST_LEFT))
    }

    @Test
    fun `a connection failing before the roster is CONNECTION_FAILED`() = runTest {
        val transport = FakeTransport()
        val room = GuestRoom(me, transport, backgroundScope)
        runCurrent()

        transport.broken(failed = true)
        runCurrent()

        Truth.assertThat(room.state.value).isEqualTo(RoomState.Closed(CloseReason.CONNECTION_FAILED))
    }

    @Test
    fun `leave sends Leave and closes`() = runTest {
        val transport = FakeTransport()
        val room = inRoom(transport)

        room.leave()
        runCurrent()

        Truth.assertThat(transport.sent.last()).isEqualTo(RoomMessage.Leave(me.id))
        Truth.assertThat(transport.closed).isTrue()
        Truth.assertThat(room.state.value).isEqualTo(RoomState.Closed(CloseReason.LEFT))
    }

    @Test
    fun `calls in a wrong state throw and send nothing`() = runTest {
        val transport = FakeTransport()
        val room = inRoom(transport)
        val sentBefore = transport.sent.toList()

        assertThrows(IllegalStateException::class.java) { room.startGame("show", GameMode.VERSUS) }
        assertThrows(IllegalStateException::class.java) { room.sendEvent("x") }
        Truth.assertThat(transport.sent).isEqualTo(sentBefore)
    }
}
