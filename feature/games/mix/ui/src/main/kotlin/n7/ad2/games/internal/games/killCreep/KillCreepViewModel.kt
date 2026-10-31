package n7.ad2.games.internal.games.killCreep

import android.os.SystemClock
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import n7.ad2.games.internal.ActiveRoom
import n7.ad2.games.leaderboard.LeaderboardSync
import n7.ad2.games.leaderboard.LocalPlayerProvider
import n7.ad2.games.leaderboard.Record
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Room
import n7.ad2.games.room.RoomState
import kotlin.random.Random

/** Всплывающая подпись над полем: «+38», «рано», «Pixel 8 забрал» */
internal data class FloatText(val id: Long, val x: Float, val y: Float, val text: String, val kind: Kind) {
    enum class Kind { GOLD, MISS, PEER }
}

internal class KillCreepViewModel @AssistedInject constructor(
    @Assisted private val mode: GameMode,
    private val leaderboardSync: LeaderboardSync,
    localPlayerProvider: LocalPlayerProvider,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(mode: GameMode): KillCreepViewModel
    }

    private val room: Room? = ActiveRoom.room.takeIf { mode != GameMode.SOLO }
    private val playing = room?.state?.value as? RoomState.Playing
    private val me = room?.me ?: localPlayerProvider.localPlayer()
    private val isHost = playing?.host?.id == me.id
    private val peerName = playing?.let { if (isHost) it.guest.name else it.host.name }

    /** Поле одинаковое у обоих игроков: seed комнаты, в соло - случайный */
    val sim = CreepSim(playing?.seed ?: Random.nextLong())
    private val startedAt = SystemClock.elapsedRealtime()
    private var floatIds = 0L

    private val _state = MutableStateFlow(State(KillCreepMatch.start(mode, isHost), peerName, emptyList(), isEndGame = false, newRecord = false))
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        viewModelScope.launch { tickAllyKillsAndTime() }
        room?.let(::observeRoom)
    }

    fun elapsedMs(): Long = SystemClock.elapsedRealtime() - startedAt

    /** [x], [y] - доли поля, [aspect] - высота/ширина поля (радиус попадания круглый на экране) */
    fun onTap(x: Float, y: Float, aspect: Float) {
        val match = _state.value.match
        if (match.isOver) return
        val time = elapsedMs()
        val creep = sim.hitTest(time, x, y, aspect)?.takeIf { !match.isTaken(it.id) } ?: return
        if (creep.isKillable) {
            update { it.myKill(creep.id, creep.type.gold, time) }
            float(creep.x, creep.y, "+${creep.type.gold} 🪙", FloatText.Kind.GOLD)
            send(KillCreepCodec.kill(creep.id, time))
        } else {
            update { it.myEarly() }
            float(creep.x, creep.y, EARLY, FloatText.Kind.MISS)
            send(KillCreepCodec.early(time))
        }
    }

    /** Раз в 100 мс: крипы, которых добили союзники, и конец по времени */
    private suspend fun tickAllyKillsAndTime() {
        val processed = mutableSetOf<Int>()
        while (!_state.value.match.isOver) {
            val time = elapsedMs()
            sim.creeps.filter { it.deathAt <= time && processed.add(it.id) }.forEach { creep -> update { it.allyKill(creep.id) } }
            if (time >= CreepSim.DURATION_MS) update { it.timeUp() }
            delay(100)
        }
    }

    private fun observeRoom(room: Room) {
        viewModelScope.launch {
            room.events.collect { event ->
                when (val message = KillCreepCodec.decode(event.payload)) {
                    is KillCreepCodec.Message.Kill -> {
                        val creep = sim.creeps.firstOrNull { it.id == message.creepId } ?: return@collect
                        val wasMine = _state.value.match.taken[creep.id]?.mine == true
                        update { it.peerKill(creep.id, creep.type.gold, message.timeMs) }
                        if (!wasMine || _state.value.match.taken[creep.id]?.mine == false) {
                            val visible = sim.visibleAt(message.timeMs).firstOrNull { it.id == creep.id }
                            float(visible?.x ?: 0.5f, visible?.y ?: 0.5f, "${peerName.orEmpty()} +${creep.type.gold}", FloatText.Kind.PEER)
                        }
                    }
                    is KillCreepCodec.Message.Early -> update { it.peerEarly() }
                    null -> Unit
                }
            }
        }
        viewModelScope.launch {
            room.state.first { it is RoomState.Closed }
            if (!_state.value.match.isOver) update { it.peerLeft() }
        }
    }

    private fun update(change: (KillCreepMatch) -> KillCreepMatch) {
        _state.update { it.copy(match = change(it.match)) }
        if (_state.value.match.isOver) finish()
    }

    private fun float(x: Float, y: Float, text: String, kind: FloatText.Kind) {
        val item = FloatText(floatIds++, x, y, text, kind)
        _state.update { it.copy(floats = it.floats + item) }
        viewModelScope.launch {
            delay(900)
            _state.update { state -> state.copy(floats = state.floats.filterNot { it.id == item.id }) }
        }
    }

    private fun send(payload: String) {
        room?.let { runCatching { it.sendEvent(payload) } }
    }

    private fun finish() {
        if (_state.value.isEndGame) return
        _state.update { it.copy(isEndGame = true) }
        val score = _state.value.match.score
        viewModelScope.launch {
            val isRecord = leaderboardSync.submit(Record(me.id, me.name, GAME_ID, mode, score, System.currentTimeMillis()))
            _state.update { it.copy(newRecord = isRecord && score > 0) }
        }
    }

    override fun onCleared() {
        if (room != null) ActiveRoom.clear()
    }

    @Immutable
    internal data class State(
        val match: KillCreepMatch,
        val peerName: String?,
        val floats: List<FloatText>,
        val isEndGame: Boolean,
        val newRecord: Boolean,
    )

    companion object {
        const val GAME_ID = "creep"
        const val EARLY = "early"
    }
}

/** Сообщения Kill Creep по сети: "k;id;мс" - добил, "e;мс" - ранний тап */
internal object KillCreepCodec {
    sealed interface Message {
        data class Kill(val creepId: Int, val timeMs: Long) : Message
        data class Early(val timeMs: Long) : Message
    }

    fun kill(creepId: Int, timeMs: Long) = "k;$creepId;$timeMs"

    fun early(timeMs: Long) = "e;$timeMs"

    fun decode(payload: String): Message? {
        val parts = payload.split(";")
        return when (parts.firstOrNull()) {
            "k" -> if (parts.size == 3) Message.Kill(parts[1].toIntOrNull() ?: return null, parts[2].toLongOrNull() ?: return null) else null
            "e" -> if (parts.size == 2) Message.Early(parts[1].toLongOrNull() ?: return null) else null
            else -> null
        }
    }
}
