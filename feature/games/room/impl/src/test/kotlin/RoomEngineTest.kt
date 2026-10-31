import com.google.common.truth.Truth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import n7.ad2.games.room.CloseReason
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Player
import n7.ad2.games.room.RoomCodec
import n7.ad2.games.room.RoomMessage
import n7.ad2.games.room.RoomState
import n7.ad2.games.room.internal.RoomEngine
import n7.ad2.games.room.internal.Transport
import n7.ad2.games.room.internal.TransportStatus
import org.junit.Assert.assertThrows
import org.junit.Test

internal class RoomEngineTest {

    private class FakeTransport : Transport {
        override val status = MutableStateFlow<TransportStatus>(TransportStatus.Connecting)
        override val incoming = MutableSharedFlow<String>(extraBufferCapacity = 16)
        val sent = mutableListOf<RoomMessage>()
        var closed = false
        override fun send(text: String) {
            sent += requireNotNull(RoomCodec.decode(text))
        }
        override fun close() {
            closed = true
        }
        fun receive(message: RoomMessage) = incoming.tryEmit(RoomCodec.encode(message))
    }

    private val host = Player("host", "Host")
    private val guest = Player("guest", "Guest")

    private fun TestScope.hostRoom(transport: FakeTransport) = RoomEngine(host, isHost = true, transport, backgroundScope, seedSource = { 42L }).also { runCurrent() }

    private fun TestScope.guestRoom(transport: FakeTransport) = RoomEngine(guest, isHost = false, transport, backgroundScope).also { runCurrent() }

    private fun TestScope.readyHost(transport: FakeTransport): RoomEngine {
        val room = hostRoom(transport)
        transport.status.value = TransportStatus.Listening(5000)
        runCurrent()
        transport.receive(RoomMessage.Hello(guest, 1))
        runCurrent()
        return room
    }

    private fun TestScope.readyGuest(transport: FakeTransport): RoomEngine {
        val room = guestRoom(transport)
        transport.status.value = TransportStatus.Connected
        runCurrent()
        transport.receive(RoomMessage.Welcome(host, guest))
        runCurrent()
        return room
    }

    private val StateFlow<RoomState>.now get() = value

    @Test
    fun `host waits then accepts guest`() = runTest {
        val transport = FakeTransport()
        val room = hostRoom(transport)

        transport.status.value = TransportStatus.Listening(5000)
        runCurrent()
        Truth.assertThat(room.state.now).isEqualTo(RoomState.Waiting(host, 5000))

        transport.receive(RoomMessage.Hello(guest, 1))
        runCurrent()
        Truth.assertThat(transport.sent).containsExactly(RoomMessage.Welcome(host, guest))
        Truth.assertThat(room.state.now).isEqualTo(RoomState.Ready(host, guest))
    }

    @Test
    fun `host rejects other protocol version`() = runTest {
        val transport = FakeTransport()
        val room = hostRoom(transport)
        transport.status.value = TransportStatus.Listening(5000)
        runCurrent()

        transport.receive(RoomMessage.Hello(guest, 2))
        runCurrent()

        Truth.assertThat(transport.sent).containsExactly(RoomMessage.Reject("version"))
        Truth.assertThat(room.state.now).isEqualTo(RoomState.Waiting(host, 5000))
    }

    @Test
    fun `host start game sends seed`() = runTest {
        val transport = FakeTransport()
        val room = readyHost(transport)

        room.startGame("spell", GameMode.VERSUS)
        runCurrent()

        Truth.assertThat(transport.sent.last()).isEqualTo(RoomMessage.StartGame("spell", GameMode.VERSUS, 42L))
        Truth.assertThat(room.state.now).isEqualTo(RoomState.Playing(host, guest, "spell", GameMode.VERSUS, 42L))
    }

    @Test
    fun `guest says hello on connect and follows host`() = runTest {
        val transport = FakeTransport()
        val room = guestRoom(transport)

        transport.status.value = TransportStatus.Connected
        runCurrent()
        Truth.assertThat(transport.sent).containsExactly(RoomMessage.Hello(guest, 1))

        transport.receive(RoomMessage.Welcome(host, guest))
        runCurrent()
        Truth.assertThat(room.state.now).isEqualTo(RoomState.Ready(host, guest))

        transport.receive(RoomMessage.StartGame("spell", GameMode.COOP, 7L))
        runCurrent()
        Truth.assertThat(room.state.now).isEqualTo(RoomState.Playing(host, guest, "spell", GameMode.COOP, 7L))
    }

    @Test
    fun `guest gets VERSION_MISMATCH on reject`() = runTest {
        val transport = FakeTransport()
        val room = guestRoom(transport)
        transport.status.value = TransportStatus.Connected
        runCurrent()

        transport.receive(RoomMessage.Reject("version"))
        runCurrent()

        Truth.assertThat(room.state.now).isEqualTo(RoomState.Closed(CloseReason.VERSION_MISMATCH))
        Truth.assertThat(transport.closed).isTrue()
    }

    @Test
    fun `events from the other player are emitted, own are not`() = runTest {
        val transport = FakeTransport()
        val room = readyHost(transport)
        room.startGame("spell", GameMode.VERSUS)
        runCurrent()
        val events = mutableListOf<RoomMessage.GameEvent>()
        backgroundScope.launch { room.events.toList(events) }
        runCurrent()

        transport.receive(RoomMessage.GameEvent("guest", "a"))
        room.sendEvent("b")
        runCurrent()

        Truth.assertThat(events).containsExactly(RoomMessage.GameEvent("guest", "a"))
        Truth.assertThat(transport.sent.last()).isEqualTo(RoomMessage.GameEvent("host", "b"))
    }

    @Test
    fun `transport closes while Playing gives PEER_LEFT`() = runTest {
        val transport = FakeTransport()
        val room = readyGuest(transport)
        transport.receive(RoomMessage.StartGame("spell", GameMode.VERSUS, 1L))
        runCurrent()

        transport.status.value = TransportStatus.Closed(failed = false)
        runCurrent()

        Truth.assertThat(room.state.now).isEqualTo(RoomState.Closed(CloseReason.PEER_LEFT))
    }

    @Test
    fun `leave message from peer gives PEER_LEFT`() = runTest {
        val transport = FakeTransport()
        val room = readyHost(transport)

        transport.receive(RoomMessage.Leave("guest"))
        runCurrent()

        Truth.assertThat(room.state.now).isEqualTo(RoomState.Closed(CloseReason.PEER_LEFT))
    }

    @Test
    fun `connect failure before Ready`() = runTest {
        val transport = FakeTransport()
        val room = guestRoom(transport)

        transport.status.value = TransportStatus.Closed(failed = true)
        runCurrent()

        Truth.assertThat(room.state.now).isEqualTo(RoomState.Closed(CloseReason.CONNECTION_FAILED))
    }

    @Test
    fun `leave sends Leave and closes`() = runTest {
        val transport = FakeTransport()
        val room = readyGuest(transport)

        room.leave()
        runCurrent()

        Truth.assertThat(transport.sent.last()).isEqualTo(RoomMessage.Leave("guest"))
        Truth.assertThat(transport.closed).isTrue()
        Truth.assertThat(room.state.now).isEqualTo(RoomState.Closed(CloseReason.LEFT))
    }

    @Test
    fun `wrong-state calls throw and send nothing`() = runTest {
        val guestTransport = FakeTransport()
        val guestRoom = readyGuest(guestTransport)
        val sentBefore = guestTransport.sent.toList()
        assertThrows(IllegalStateException::class.java) { guestRoom.startGame("spell", GameMode.VERSUS) }
        Truth.assertThat(guestTransport.sent).isEqualTo(sentBefore)

        val hostTransport = FakeTransport()
        val hostRoom = hostRoom(hostTransport)
        hostTransport.status.value = TransportStatus.Listening(5000)
        runCurrent()
        assertThrows(IllegalStateException::class.java) { hostRoom.sendEvent("x") }
        Truth.assertThat(hostTransport.sent).isEmpty()
    }

    @Test
    fun `sync is delivered in Ready and Playing`() = runTest {
        val transport = FakeTransport()
        val room = readyHost(transport)
        val syncs = mutableListOf<RoomMessage.Sync>()
        backgroundScope.launch { room.syncEvents.toList(syncs) }
        runCurrent()

        room.sendSync("records", "a")
        transport.receive(RoomMessage.Sync("records", "b"))
        runCurrent()
        room.startGame("spell", GameMode.SOLO)
        room.sendSync("records", "c")
        runCurrent()

        Truth.assertThat(syncs).containsExactly(RoomMessage.Sync("records", "b"))
        Truth.assertThat(transport.sent.filterIsInstance<RoomMessage.Sync>()).containsExactly(RoomMessage.Sync("records", "a"), RoomMessage.Sync("records", "c")).inOrder()
    }

    @Test
    fun `sendSync in Waiting throws and sends nothing`() = runTest {
        val transport = FakeTransport()
        val room = hostRoom(transport)
        transport.status.value = TransportStatus.Listening(5000)
        runCurrent()

        assertThrows(IllegalStateException::class.java) { room.sendSync("records", "a") }
        Truth.assertThat(transport.sent).isEmpty()
    }

    @Test
    fun `garbage on the wire is ignored`() = runTest {
        val transport = FakeTransport()
        val room = readyHost(transport)

        transport.incoming.tryEmit("not json")
        runCurrent()

        Truth.assertThat(room.state.now).isEqualTo(RoomState.Ready(host, guest))
    }
}
