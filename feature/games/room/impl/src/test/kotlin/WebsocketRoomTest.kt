import com.google.common.truth.Truth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.debug.junit4.CoroutinesTimeout
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import n7.ad2.games.room.CloseReason
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.MAX_PLAYERS
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

    @get:Rule val timeout = CoroutinesTimeout.seconds(15)

    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val factory = WebsocketRoomFactory(scope)
    private val loopback = InetAddress.getLoopbackAddress()
    private val hostPlayer = Player("h", "Host")

    @After
    fun tearDown() = scope.cancel()

    private suspend fun hostRoom(): Pair<Room, Int> {
        val host = factory.host(hostPlayer, loopback)
        val port = (host.state.first { it is RoomState.Waiting } as RoomState.Waiting).port
        return host to port
    }

    /** Guests join one after another, each waits until it is in */
    private suspend fun join(port: Int, vararg players: Player): List<Room> = players.map { player ->
        factory.join(player, loopback, port).also { guest -> guest.state.first { it is RoomState.Ready || it is RoomState.Closed } }
    }

    private suspend fun Room.awaitPlayers(count: Int): List<Player> {
        val state = state.first { (it as? RoomState.Ready)?.players?.size == count || (it as? RoomState.Playing)?.players?.size == count }
        return (state as? RoomState.Ready)?.players ?: (state as RoomState.Playing).players
    }

    @Test
    fun `two guests join the host and all three see the same roster`() = runBlocking {
        val (host, port) = hostRoom()
        val anna = Player("a", "Анна")
        val boris = Player("b", "Boris")

        val guests = join(port, anna, boris)

        val roster = listOf(hostPlayer, anna, boris)
        Truth.assertThat(host.awaitPlayers(3)).isEqualTo(roster)
        guests.forEach { guest -> Truth.assertThat(guest.awaitPlayers(3)).isEqualTo(roster) }
    }

    @Test
    fun `the start reaches every guest with one seed and an event of a guest reaches everyone else`() = runBlocking {
        val (host, port) = hostRoom()
        val (anna, boris) = join(port, Player("a", "Anna"), Player("b", "Boris"))
        anna.awaitPlayers(3)

        host.startGame("show", GameMode.VERSUS)
        val seed = (host.state.value as RoomState.Playing).seed
        listOf(anna, boris).forEach { guest ->
            Truth.assertThat((guest.state.first { it is RoomState.Playing } as RoomState.Playing).seed).isEqualTo(seed)
        }

        val atHost = async { host.events.first() }
        val atBoris = async { boris.events.first() }
        // the subscriptions are in place before sending
        delay(100)
        anna.sendEvent("x".repeat(500))

        Truth.assertThat(atHost.await().playerId).isEqualTo("a")
        Truth.assertThat(atBoris.await().payload).isEqualTo("x".repeat(500))
    }

    @Test
    fun `a guest leaving shrinks the roster for everyone`() = runBlocking {
        val (host, port) = hostRoom()
        val anna = Player("a", "Anna")
        val (annaRoom, borisRoom) = join(port, anna, Player("b", "Boris"))
        annaRoom.awaitPlayers(3)

        borisRoom.leave()

        Truth.assertThat(host.awaitPlayers(2)).isEqualTo(listOf(hostPlayer, anna))
        Truth.assertThat(annaRoom.awaitPlayers(2)).isEqualTo(listOf(hostPlayer, anna))
    }

    @Test
    fun `the host leaving closes the room for every guest as HOST_LEFT`() = runBlocking {
        val (host, port) = hostRoom()
        val guests = join(port, Player("a", "Anna"), Player("b", "Boris"))
        guests.first().awaitPlayers(3)

        host.leave()

        guests.forEach { guest -> Truth.assertThat(guest.state.first { it is RoomState.Closed }).isEqualTo(RoomState.Closed(CloseReason.HOST_LEFT)) }
    }

    @Test
    fun `a player too many is turned away as FULL and nobody joins during a game as BUSY`() = runBlocking {
        val (host, port) = hostRoom()
        join(port, *(1 until MAX_PLAYERS).map { Player("p$it", "P$it") }.toTypedArray())
        host.awaitPlayers(MAX_PLAYERS)

        val late = factory.join(Player("late", "Late"), loopback, port)
        Truth.assertThat(late.state.first { it is RoomState.Closed }).isEqualTo(RoomState.Closed(CloseReason.FULL))

        val (smallHost, smallPort) = hostRoom()
        join(smallPort, Player("a", "Anna"))
        smallHost.awaitPlayers(2)
        smallHost.startGame("show", GameMode.VERSUS)
        val busy = factory.join(Player("b", "Boris"), loopback, smallPort)
        Truth.assertThat(busy.state.first { it is RoomState.Closed }).isEqualTo(RoomState.Closed(CloseReason.BUSY))
    }

    @Test
    fun `a connection that never says hello does not break the room`() = runBlocking {
        val (host, port) = hostRoom()
        java.net.Socket(loopback, port).close()

        val (anna) = join(port, Player("a", "Anna"))

        Truth.assertThat(anna.state.value).isEqualTo(RoomState.Ready(listOf(hostPlayer, Player("a", "Anna"))))
        Truth.assertThat(host.awaitPlayers(2)).hasSize(2)
    }

    @Test
    fun `join to a closed port ends CONNECTION_FAILED`() = runBlocking {
        val closedPort = ServerSocket(0).use { it.localPort }

        val guest = factory.join(Player("g", "Гость"), loopback, closedPort)

        Truth.assertThat(guest.state.first { it is RoomState.Closed }).isEqualTo(RoomState.Closed(CloseReason.CONNECTION_FAILED))
    }
}
