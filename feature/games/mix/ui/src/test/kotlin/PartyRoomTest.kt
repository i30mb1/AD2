import com.google.common.truth.Truth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import n7.ad2.feature.games.xo.domain.DiscoverServicesInNetworkUseCase
import n7.ad2.feature.games.xo.domain.GetNetworkStateUseCase
import n7.ad2.feature.games.xo.domain.RegisterServiceInNetworkUseCase
import n7.ad2.feature.games.xo.domain.WifiDirect
import n7.ad2.feature.games.xo.domain.model.NetworkState
import n7.ad2.feature.games.xo.domain.model.Server
import n7.ad2.feature.games.xo.domain.model.SimpleServer
import n7.ad2.feature.games.xo.domain.model.WifiDirectRoom
import n7.ad2.games.internal.party.Party
import n7.ad2.games.internal.party.PartyState
import n7.ad2.games.leaderboard.LeaderboardRepository
import n7.ad2.games.leaderboard.LeaderboardSync
import n7.ad2.games.leaderboard.Record
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Player
import n7.ad2.games.room.WebsocketRoomFactory
import org.junit.After
import org.junit.Test
import java.net.InetAddress

/** Three phones on the loopback: the real rooms over websocket, the network and Wi-Fi Direct faked */
@OptIn(ExperimentalCoroutinesApi::class)
internal class PartyRoomTest {

    // the party lives on one thread as on the main one
    private val partyThread = newSingleThreadContext("party")
    private val partyScope = CoroutineScope(SupervisorJob() + partyThread)
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val roomFactory = WebsocketRoomFactory(ioScope)
    private val parties = mutableListOf<Party>()

    private object NoRecords : LeaderboardRepository {
        override fun observe(gameId: String, mode: GameMode): Flow<List<Record>> = emptyFlow()
        override suspend fun all(): List<Record> = emptyList()
        override suspend fun submit(record: Record): Boolean = false
        override suspend fun merge(records: List<Record>): List<Record> = emptyList()
    }

    private object Loopback : GetNetworkStateUseCase {
        override fun invoke(): Flow<NetworkState> = flowOf(NetworkState.Connected(InetAddress.getLoopbackAddress().hostAddress.orEmpty()))
    }

    private object NobodyAround : DiscoverServicesInNetworkUseCase {
        override fun invoke(): Flow<List<Server>> = flowOf(emptyList())
    }

    private object NoNsd : RegisterServiceInNetworkUseCase {
        override suspend fun register(server: SimpleServer): SimpleServer = server
        override fun unregister() = Unit
    }

    private object NoWifiDirect : WifiDirect {
        override suspend fun advertise(name: String, port: Int, playerId: String): Boolean = false
        override fun discover(): Flow<List<WifiDirectRoom>> = flowOf(emptyList())
        override suspend fun connect(deviceAddress: String): InetAddress? = null
        override fun release() = Unit
    }

    private suspend fun party(id: String, name: String): Party = onParty {
        Party(Player(id, name), partyScope, roomFactory, LeaderboardSync(NoRecords, partyScope), Loopback, NobodyAround, NoNsd, NoWifiDirect, clock = { 0L })
            .also { parties += it; it.setVisible(true) }
    }

    private suspend fun <T> onParty(block: () -> T): T = withContext(partyThread) { block() }

    private suspend fun Party.awaitState(check: (PartyState) -> Boolean): PartyState = withTimeout(10_000) { state.first(check) }

    private suspend fun Party.address(): String = requireNotNull(awaitState { it.myAddress != null }.myAddress)

    private suspend fun Party.awaitPlayers(count: Int) = awaitState { it.session?.players?.size == count }

    @After
    fun tearDown() = runBlocking<Unit> {
        onParty { parties.forEach { it.release() } }
        partyScope.cancel()
        ioScope.cancel()
        partyThread.close()
    }

    /** The host and two guests in one room */
    private suspend fun room(): Triple<Party, Party, Party> {
        val host = party("h", "Host")
        val anna = party("a", "Anna")
        val boris = party("b", "Boris")
        val address = host.address()
        onParty { anna.joinManual(address) }
        anna.awaitPlayers(2)
        onParty { boris.joinManual(address) }
        listOf(host, anna, boris).forEach { it.awaitPlayers(3) }
        return Triple(host, anna, boris)
    }

    @Test(timeout = 30_000)
    fun `guests come into the room of the host, the pick and the readiness go around and the start opens the game everywhere`() = runBlocking<Unit> {
        val (host, anna, boris) = room()
        Truth.assertThat(host.state.value.session?.isHost).isTrue()
        Truth.assertThat(anna.state.value.session?.players?.map { it.id }).containsExactly("h", "a", "b").inOrder()

        onParty { host.pick("show", GameMode.COOP) }
        anna.awaitState { it.session?.lobby?.gameId == "show" && it.session?.lobby?.mode == GameMode.COOP }
        onParty {
            anna.setReady(true)
            boris.setReady(true)
        }
        host.awaitState { it.session?.lobby?.ready == setOf("a", "b") }
        boris.awaitState { it.session?.lobby?.ready == setOf("a", "b") }

        val opened = listOf(host, anna, boris).map { party -> async { withTimeout(10_000) { party.openGame.first() } } }
        delay(100)
        onParty { host.start() }
        val games = opened.map { it.await() }

        Truth.assertThat(games.map { it.gameId }.toSet()).containsExactly("show")
        Truth.assertThat(games.map { it.seed }.toSet()).hasSize(1)
        Truth.assertThat(games.first().players.map { it.id }).containsExactly("h", "a", "b").inOrder()
        // the next game asks everyone to be ready again
        host.awaitState { it.session?.lobby?.ready?.isEmpty() == true }
    }

    @Test(timeout = 30_000)
    fun `a game for two does not start with three and does once one of them leaves`() = runBlocking<Unit> {
        val (host, anna, boris) = room()

        onParty {
            host.pick("xo", GameMode.VERSUS)
        }
        boris.awaitState { it.session?.lobby?.gameId == "xo" }
        onParty {
            anna.setReady(true)
            boris.setReady(true)
        }
        host.awaitState { it.session?.lobby?.ready == setOf("a", "b") }
        onParty { host.start() }
        delay(300)
        Truth.assertThat(onParty { host.state.value.session?.players?.size }).isEqualTo(3)

        onParty { boris.leave() }
        host.awaitPlayers(2)
        anna.awaitPlayers(2)
        val opened = async { withTimeout(10_000) { anna.openGame.first() } }
        delay(100)
        onParty { host.start() }

        Truth.assertThat(opened.await().gameId).isEqualTo("xo")
    }

    @Test(timeout = 30_000)
    fun `the host leaving sends the guests back to the list`() = runBlocking<Unit> {
        val (host, anna, boris) = room()

        onParty { host.leave() }

        anna.awaitState { it.session == null }
        boris.awaitState { it.session == null }
        Truth.assertThat(onParty { host.state.value.session }).isNull()
    }

    @Test(timeout = 30_000)
    fun `all guests leaving leave the host alone and the room open for the next ones`() = runBlocking<Unit> {
        val (host, anna, boris) = room()
        val address = host.address()

        onParty {
            anna.leave()
            boris.leave()
        }
        host.awaitState { it.session == null }
        onParty { anna.joinManual(address) }

        host.awaitPlayers(2)
        Truth.assertThat(anna.awaitPlayers(2).session?.host?.id).isEqualTo("h")
    }
}
