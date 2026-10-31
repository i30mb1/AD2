package n7.ad2.games.internal.games.skillmp

import android.graphics.Color
import android.os.SystemClock
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
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
import kotlin.time.Duration.Companion.seconds

internal class SkillGameViewModel @AssistedInject constructor(
    @Assisted private val mode: GameMode,
    @Assisted private val gameId: String,
    private val getSkillsUseCase: GetSkillsUseCase,
    private val getHeroQuizUseCase: GetHeroQuizUseCase,
    private val getCompareQuizUseCase: GetCompareQuizUseCase,
    private val leaderboardSync: LeaderboardSync,
    localPlayerProvider: LocalPlayerProvider,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(mode: GameMode, gameId: String): SkillGameViewModel
    }

    private val room: Room? = ActiveRoom.room.takeIf { mode != GameMode.SOLO }
    private val playing = room?.state?.value as? RoomState.Playing
    private val seed: Long? = playing?.seed
    private val me = room?.me ?: localPlayerProvider.localPlayer()
    private val peerName = playing?.let { if (it.host.id == me.id) it.guest.name else it.host.name }

    private val _state = MutableStateFlow(State.init(SpellMatch.start(mode), peerName))
    val state: StateFlow<State> = _state.asStateFlow()

    private val pendingPeerAnswers = mutableMapOf<Int, Answer>()
    private var questionShownAt = 0L
    private var advancingRound = -1
    private var questionJob: Job? = null

    init {
        loadQuestion()
        room?.let(::observeRoom)
    }

    fun onAnswer(spell: Spell) {
        val current = _state.value
        if (current.isLoading || current.showRightAnswer || current.match.isOver) return
        val answer = Answer(spell.isRightAnswer, SystemClock.elapsedRealtime() - questionShownAt)
        _state.update { it.copy(showRightAnswer = true, selectedSpell = spell, match = it.match.answer(mine = true, answer)) }
        room?.let { runCatching { it.sendEvent(PeerAnswerCodec.encode(current.match.round, answer)) } }
        advanceIfRoundComplete()
    }

    private fun observeRoom(room: Room) {
        viewModelScope.launch {
            room.events.collect { event ->
                val (round, answer) = PeerAnswerCodec.decode(event.payload) ?: return@collect
                if (round == _state.value.match.round) {
                    _state.update { it.copy(match = it.match.answer(mine = false, answer)) }
                    advanceIfRoundComplete()
                } else if (round > _state.value.match.round) {
                    pendingPeerAnswers[round] = answer
                }
            }
        }
        viewModelScope.launch {
            room.state.first { it is RoomState.Closed }
            if (!_state.value.match.isOver) {
                _state.update { it.copy(match = it.match.peerLeft()) }
                finish()
            }
        }
    }

    private fun advanceIfRoundComplete() {
        val match = _state.value.match
        if (!match.roundComplete || advancingRound == match.round) return
        advancingRound = match.round
        viewModelScope.launch {
            delay(1.seconds)
            if (_state.value.match.isOver) {
                finish()
            } else {
                _state.update { it.copy(match = it.match.nextRound()) }
                loadQuestion()
            }
        }
    }

    private fun loadQuestion() {
        questionJob?.cancel()
        val round = _state.value.match.round
        questionJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, showRightAnswer = false, selectedSpell = null) }
            delay(0.2.seconds)
            val data = loadData(round) ?: return@launch _state.update { it.copy(isLoading = false, isError = true) }
            _state.update { state ->
                state.copy(
                    isLoading = false,
                    backgroundColor = data.backgroundColor,
                    spellImage = data.skillImage,
                    spellList = SpellList(data.suggestsSpellList.map { Spell(it.cost, it.isRightAnswer) }),
                    spellLVL = data.spellLVL,
                    compare = data.compare,
                )
            }
            questionShownAt = SystemClock.elapsedRealtime()
            pendingPeerAnswers.remove(round)?.let { answer ->
                _state.update { it.copy(match = it.match.answer(mine = false, answer)) }
                advanceIfRoundComplete()
            }
        }
    }

    /**
     * В сетевой игре попытка k берет Random(seed, раунд, k): если у выпавшего героя нет подходящего спелла,
     * оба телефона одинаково переходят к следующей попытке и получают один и тот же вопрос
     */
    private suspend fun loadData(round: Int): GetSkillsUseCase.Data? {
        repeat(MAX_ATTEMPTS) { attempt ->
            val random = seed?.let { Random(it + round * MAX_ATTEMPTS + attempt) } ?: Random.Default
            val question = when (gameId) {
                HERO_GAME_ID -> getHeroQuizUseCase(random)
                COMPARE_GAME_ID -> getCompareQuizUseCase(random)
                else -> getSkillsUseCase(random)
            }
            runCatching { question.first() }.onSuccess { return it }
        }
        return null
    }

    private fun finish() {
        if (_state.value.isEndGame) return
        _state.update { it.copy(isEndGame = true) }
        val score = _state.value.match.myScore
        viewModelScope.launch {
            val isRecord = leaderboardSync.submit(Record(me.id, me.name, gameId, mode, score, System.currentTimeMillis()))
            _state.update { it.copy(newRecord = isRecord && score > 0) }
        }
    }

    override fun onCleared() {
        if (room != null) ActiveRoom.clear()
    }

    @Immutable
    internal data class State(
        val isLoading: Boolean,
        val isError: Boolean,
        val backgroundColor: Int,
        val spellImage: String,
        val spellList: SpellList,
        val spellLVL: Int,
        val showRightAnswer: Boolean,
        val selectedSpell: Spell?,
        val match: SpellMatch,
        val compare: GetSkillsUseCase.Compare?,
        val peerName: String?,
        val isEndGame: Boolean,
        val newRecord: Boolean,
    ) {
        companion object {
            fun init(match: SpellMatch, peerName: String?) = State(
                isLoading = true,
                isError = false,
                backgroundColor = Color.TRANSPARENT,
                spellImage = "",
                spellList = SpellList(emptyList()),
                spellLVL = 0,
                showRightAnswer = false,
                selectedSpell = null,
                match = match,
                compare = null,
                peerName = peerName,
                isEndGame = false,
                newRecord = false,
            )
        }
    }

    @Immutable
    internal data class SpellList(val list: List<Spell>)

    @Immutable
    internal data class Spell(val cost: String, val isRightAnswer: Boolean)

    companion object {
        const val GAME_ID = "spell"
        const val HERO_GAME_ID = "hero"
        const val COMPARE_GAME_ID = "compare"
        private const val MAX_ATTEMPTS = 20
    }
}

/** Ответ соперника в формате "раунд;верно;мс" */
internal object PeerAnswerCodec {
    fun encode(round: Int, answer: Answer): String = "$round;${answer.correct};${answer.timeMs}"

    fun decode(payload: String): Pair<Int, Answer>? {
        val parts = payload.split(";")
        if (parts.size != 3) return null
        val round = parts[0].toIntOrNull() ?: return null
        val correct = parts[1].toBooleanStrictOrNull() ?: return null
        val time = parts[2].toLongOrNull() ?: return null
        return round to Answer(correct, time)
    }
}
