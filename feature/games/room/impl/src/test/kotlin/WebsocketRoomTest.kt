import com.google.common.truth.Truth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.debug.junit4.CoroutinesTimeout
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import n7.ad2.games.room.CloseReason
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Player
import n7.ad2.games.room.Room
import n7.ad2.games.room.RoomState
import n7.ad2.games.room.WebsocketRoomFactory
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.net.InetAddress
import java.net.ServerSocket

internal class WebsocketRoomTest {

    @get:Rule val timeout = CoroutinesTimeout.seconds(10)

    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val factory = WebsocketRoomFactory(scope)
    private val loopback = InetAddress.getLoopbackAddress()
    private val hostPlayer = Player("h", "Host")
    private val guestPlayer = Player("g", "Гость")

    @After
    fun tearDown() = scope.cancel()

    private suspend fun connectedPair(): Pair<Room, Room> {
        val host = factory.host(hostPlayer, loopback)
        val port = (host.state.first { it is RoomState.Waiting } as RoomState.Waiting).port
        val guest = factory.join(guestPlayer, loopback, port)
        host.state.first { it is RoomState.Ready }
        guest.state.first { it is RoomState.Ready }
        return host to guest
    }

    @Test
    fun `guest joins host and both reach Ready`() = runBlocking {
        val (host, guest) = connectedPair()

        Truth.assertThat(host.state.value).isEqualTo(RoomState.Ready(hostPlayer, guestPlayer))
        Truth.assertThat(guest.state.value).isEqualTo(RoomState.Ready(hostPlayer, guestPlayer))
    }

    @Test
    fun `start game reaches guest with the same seed and events flow both ways`() = runBlocking {
        val (host, guest) = connectedPair()

        host.startGame("spell", GameMode.VERSUS)
        val hostPlaying = host.state.value as RoomState.Playing
        val guestPlaying = guest.state.first { it is RoomState.Playing } as RoomState.Playing
        Truth.assertThat(guestPlaying.seed).isEqualTo(hostPlaying.seed)

        val longPayload = "x".repeat(500)
        val atGuest = async { guest.events.first() }
        val atHost = async { host.events.first() }
        // подписки успевают оформиться до отправки
        kotlinx.coroutines.delay(100)
        host.sendEvent(longPayload)
        guest.sendEvent("y")

        Truth.assertThat(atGuest.await().payload).isEqualTo(longPayload)
        Truth.assertThat(atHost.await().payload).isEqualTo("y")
    }

    @Test
    fun `host leaves and guest sees PEER_LEFT`() = runBlocking {
        val (host, guest) = connectedPair()

        host.leave()

        Truth.assertThat(guest.state.first { it is RoomState.Closed }).isEqualTo(RoomState.Closed(CloseReason.PEER_LEFT))
    }

    @Test
    fun `join to a closed port ends CONNECTION_FAILED`() = runBlocking {
        val closedPort = ServerSocket(0).use { it.localPort }

        val guest = factory.join(guestPlayer, loopback, closedPort)

        Truth.assertThat(guest.state.first { it is RoomState.Closed }).isEqualTo(RoomState.Closed(CloseReason.CONNECTION_FAILED))
    }
}
