import com.google.common.truth.Truth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import n7.ad2.games.internal.party.GameChannel
import n7.ad2.games.room.CloseReason
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Player
import n7.ad2.games.room.Room
import n7.ad2.games.room.RoomMessage
import n7.ad2.games.room.RoomState
import org.junit.Test

internal class GameChannelTest {

    private val me = Player("me", "Me")
    private val anna = Player("a", "Anna")
    private val boris = Player("b", "Boris")

    private class FakeRoom(override val me: Player, initial: RoomState) : Room {
        override val state = MutableStateFlow(initial)
        override val events: Flow<RoomMessage.GameEvent> = emptyFlow()
        val syncs = MutableSharedFlow<RoomMessage.Sync>(extraBufferCapacity = 16)
        override val syncEvents: Flow<RoomMessage.Sync> = syncs
        val sent = mutableListOf<RoomMessage.Sync>()
        override fun sendSync(channel: String, payload: String) {
            sent += RoomMessage.Sync(channel, payload, me.id)
        }
        override fun startGame(gameId: String, mode: GameMode) = Unit
        override fun endGame() = Unit
        override fun sendEvent(payload: String) = Unit
        override fun leave() = Unit
    }

    private fun playing(vararg players: Player) = RoomState.Playing(players.toList(), "show", GameMode.VERSUS, 1L)

    @Test
    fun `a player who quits or is gone from the room leaves once, a closed room takes everyone else`() = runBlocking {
        val room = FakeRoom(me, playing(me, anna, boris))
        val left = mutableListOf<String>()
        val job = launch { GameChannel.departures(room, setOf(anna.id, boris.id)).take(2).toList(left) }
        yield()

        GameChannel.quit(room)
        room.syncs.emit(room.sent.single().copy(playerId = anna.id))
        yield()
        room.state.value = playing(me, boris)
        yield()
        room.state.value = RoomState.Closed(CloseReason.HOST_LEFT)
        job.join()

        Truth.assertThat(left).containsExactly(anna.id, boris.id).inOrder()
    }

    @Test
    fun `the end of my game is not a departure`() = runBlocking {
        val room = FakeRoom(me, playing(me, anna))
        val left = mutableListOf<String>()
        val job = launch { GameChannel.departures(room, setOf(anna.id)).toList(left) }
        yield()

        room.state.value = RoomState.Ready(listOf(me, anna))
        yield()
        job.cancel()

        Truth.assertThat(left).isEmpty()
    }
}
