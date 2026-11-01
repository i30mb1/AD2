package n7.ad2.games.internal.games.show

import android.os.SystemClock
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import n7.ad2.games.internal.ActiveRoom
import n7.ad2.games.internal.MatchClock
import n7.ad2.games.internal.party.GameChannel
import n7.ad2.games.internal.party.Pick
import n7.ad2.games.leaderboard.LeaderboardRepository
import n7.ad2.games.leaderboard.LeaderboardSync
import n7.ad2.games.leaderboard.LocalPlayerProvider
import n7.ad2.games.leaderboard.Record
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Player
import n7.ad2.games.room.Room
import n7.ad2.games.room.RoomState

/** The voice of a «Кто сказал?» round */
internal enum class Voice { NONE, LOADING, PLAYING, READY, FAILED }

/**
 * The quiz show: [ShowPlan.ROUNDS] rounds of different kinds, the same for everyone in the room.
 * Each phone counts its own points and tells the others; the next round starts when everyone answered.
 * Versus - who has more, together - one team score.
 * [daily] - the challenge of the day: [DailyPlan.QUESTIONS] questions from the date, without bets and helps, once a day.
 */
internal class ShowViewModel @AssistedInject constructor(
    @Assisted private val mode: GameMode,
    @Assisted private val daily: Boolean,
    private val questions: ShowQuestions,
    private val dailyStore: DailyStore,
    private val leaderboardSync: LeaderboardSync,
    private val leaderboardRepository: LeaderboardRepository,
    localPlayerProvider: LocalPlayerProvider,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(mode: GameMode, daily: Boolean): ShowViewModel
    }

    private val room: Room? = ActiveRoom.room.takeIf { mode != GameMode.SOLO }
    private val playing = room?.state?.value as? RoomState.Playing
    private val me = room?.me ?: localPlayerProvider.localPlayer()
    private val today: LocalDate = LocalDate.now()
    private val seed = if (daily) DailyPlan.seed(today) else playing?.seed ?: Random.nextLong()
    private val rounds = if (daily) DailyPlan.create() else ShowPlan.create(seed)
    private val players = (listOf(me) + playing?.others(me.id).orEmpty()).map { it.toShowPlayer() }

    private val voice = VoicePlayer()
    private var tickJob: Job? = null
    private var advancing = -1

    private val _state = MutableStateFlow(State.start(mode, me.id, ShowMatch(players, rounds), daily, today))
    val state: StateFlow<State> = _state.asStateFlow()

    /** The result screen is closed («Again» or «Another game»): back to the games list */
    private val _exit = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val exit: SharedFlow<Unit> = _exit.asSharedFlow()

    init {
        room?.let(::observeRoom)
        if (daily) observeNearby()
        val played = if (daily) dailyStore.result(today) else null
        if (played != null) {
            showPlayed(played)
        } else {
            viewModelScope.launch {
                if (room != null) countdown()
                startRound()
            }
        }
    }

    /** The result of the day for a chat */
    fun shareText(title: String): String = dailyShareText(title, dailyResult(_state.value.match))

    fun again() {
        ActiveRoom.requestRematch(Pick(GAME_ID, mode))
        _exit.tryEmit(Unit)
    }

    fun otherGame() {
        _exit.tryEmit(Unit)
    }

    /** During the intro of a round: points ×2 for a right answer, minus 100 for a wrong one; once a show, not in the final */
    fun toggleBet() {
        val state = _state.value
        if (daily || state.phase != State.Phase.INTRO || state.betUsed || state.match.current.final) return
        _state.update { it.copy(bet = !it.bet) }
    }

    fun pick(index: Int) {
        val state = _state.value
        if (!state.canAnswer || index in state.hidden) return
        val option = state.question?.options?.getOrNull(index) ?: return
        _state.update { it.copy(picked = index) }
        submit(option.isRight)
    }

    /** «Собери предмет»: a tap puts a tile into a slot, another tap takes it back */
    fun toggleTile(index: Int) {
        val state = _state.value
        val build = (state.question as? ShowQuestion.Build)?.question ?: return
        if (!state.canAnswer) return
        _state.update {
            when {
                index in it.tiles -> it.copy(tiles = it.tiles - index)
                it.tiles.size < build.slots -> it.copy(tiles = it.tiles + index)
                else -> it
            }
        }
    }

    fun build() {
        val state = _state.value
        val build = (state.question as? ShowQuestion.Build)?.question ?: return
        if (!state.canAnswer || state.tiles.size != build.slots) return
        submit(state.tiles.all { build.tiles[it].isCorrect })
    }

    /** The final: the answers open for the one who pressed; the earlier, the more points */
    fun buzz() {
        val state = _state.value
        if (state.phase != State.Phase.QUESTION || !state.match.current.final || state.buzzShare != null || state.myAnswer != null) return
        val now = SystemClock.elapsedRealtime()
        _state.update { it.copy(buzzShare = it.fuse?.share(now) ?: 0f, answerFuse = Fuse(now, FINAL_ANSWER_MS)) }
    }

    fun fiftyFifty() {
        val state = _state.value
        val question = state.question as? ShowQuestion.Pick ?: return
        if (daily || !state.canAnswer || state.fiftyUsed) return
        val right = question.options.indexOfFirst { it.isRight }
        _state.update { it.copy(fiftyUsed = true, hidden = fiftyFifty(right, question.options.size, Random(seed + it.match.round))) }
    }

    /** Stops the fuse that is burning now: in the final after the buzz - the one for the answer */
    fun freeze() {
        val state = _state.value
        if (daily || state.phase != State.Phase.QUESTION || state.freezeUsed || state.myAnswer != null) return
        val now = SystemClock.elapsedRealtime()
        _state.update {
            if (it.answerFuse != null) it.copy(freezeUsed = true, answerFuse = it.answerFuse.freeze(now)) else it.copy(freezeUsed = true, fuse = it.fuse?.freeze(now))
        }
    }

    fun replayVoice() {
        val state = _state.value
        if (state.voice != Voice.READY || state.replaysLeft <= 0 || state.phase != State.Phase.QUESTION) return
        _state.update { it.copy(replaysLeft = it.replaysLeft - 1) }
        playVoice()
    }

    // the common start: phones open the game at slightly different moments
    private suspend fun countdown() {
        val clock = MatchClock(ActiveRoom.startedAt, COUNTDOWN_MS)
        while (true) {
            val left = clock.countdown(SystemClock.elapsedRealtime())
            _state.update { it.copy(countdown = left) }
            if (left == 0) break
            delay(100)
        }
    }

    /** The intro shows the kind of the round while its question (and the sound of a voice line) loads */
    private suspend fun startRound() {
        val round = _state.value.match.current
        _state.update { it.forRound() }
        val loading = viewModelScope.async { prepare(round) }
        delay(if (daily) DAILY_INTRO_MS else INTRO_MS)
        val (question, voiceState) = loading.await() ?: return _state.update { it.copy(loadFailed = true) }
        val now = SystemClock.elapsedRealtime()
        _state.update {
            it.copy(
                phase = State.Phase.QUESTION,
                question = question,
                voice = voiceState,
                fuse = Fuse(now, fuseMs(round, question)),
                betUsed = it.betUsed || it.bet,
            )
        }
        if (voiceState == Voice.READY) playVoice()
        startTicker()
    }

    private suspend fun prepare(round: ShowRound): Pair<ShowQuestion, Voice>? {
        val question = load(round) ?: return null
        val line = (question as? ShowQuestion.Pick)?.voice ?: return question to Voice.NONE
        val ready = withTimeoutOrNull(VOICE_WAIT_MS) { voice.prepare(line.audioUrl) } == true
        return question to if (ready) Voice.READY else Voice.FAILED
    }

    /** Attempt k takes Random(seed, round, k): when a hero has no fitting spell, every phone goes to the same next attempt */
    private suspend fun load(round: ShowRound): ShowQuestion? {
        repeat(MAX_ATTEMPTS) { attempt ->
            try {
                return questions.load(round.kind, Random(seed + round.index * MAX_ATTEMPTS + attempt))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // the next attempt
            }
        }
        return null
    }

    private fun playVoice() {
        _state.update { it.copy(voice = Voice.PLAYING) }
        voice.play { _state.update { if (it.voice == Voice.PLAYING) it.copy(voice = Voice.READY) else it } }
    }

    /** A burned fuse is an answer too: a wrong one, in the final without the buzz - no points at all */
    private fun startTicker() {
        tickJob?.cancel()
        tickJob = viewModelScope.launch {
            while (isActive) {
                val state = _state.value
                if (state.phase != State.Phase.QUESTION || state.myAnswer != null) break
                val now = SystemClock.elapsedRealtime()
                val burned = state.answerFuse?.let { it.left(now) == 0L } ?: (state.fuse?.left(now) == 0L)
                if (burned) submit(correct = false)
                delay(TICK_MS)
            }
        }
    }

    private fun submit(correct: Boolean) {
        val state = _state.value
        if (state.phase != State.Phase.QUESTION || state.myAnswer != null) return
        val round = state.match.current
        val now = SystemClock.elapsedRealtime()
        val points = if (round.final) ShowScoring.final(correct, state.buzzShare) else ShowScoring.round(correct, state.fuse?.share(now) ?: 0f, state.bet)
        val answer = RoundAnswer(correct, points)
        voice.stop()
        _state.update {
            it.copy(
                phase = State.Phase.REVEAL,
                myAnswer = answer,
                voice = if (it.voice == Voice.PLAYING) Voice.READY else it.voice,
                match = it.match.answer(me.id, round.index, answer),
            )
        }
        room?.let { runCatching { it.sendEvent(ShowCodec.encode(round.index, answer)) } }
        advanceIfComplete()
    }

    private fun observeRoom(room: Room) {
        viewModelScope.launch {
            room.events.collect { event ->
                val (round, answer) = ShowCodec.decode(event.payload) ?: return@collect
                _state.update { it.copy(match = it.match.answer(event.playerId, round, answer)) }
                advanceIfComplete()
            }
        }
        viewModelScope.launch {
            GameChannel.departures(room, players.map { it.id }.toSet() - me.id).collect(::left)
        }
    }

    /** A player left the show: the rounds do not wait for them any more */
    private fun left(playerId: String) {
        if (_state.value.phase == State.Phase.END) return
        _state.update { state -> state.copy(match = state.match.leave(playerId)) }
        if (_state.value.match.isOver) finish() else advanceIfComplete()
    }

    private fun advanceIfComplete() {
        val match = _state.value.match
        if (!match.roundComplete || advancing == match.round || _state.value.phase == State.Phase.END) return
        advancing = match.round
        viewModelScope.launch {
            delay(REVEAL_MS)
            _state.update { it.copy(match = it.match.nextRound()) }
            if (_state.value.match.isOver) finish() else startRound()
        }
    }

    private fun finish() {
        if (_state.value.phase == State.Phase.END) return
        tickJob?.cancel()
        voice.release()
        _state.update { it.copy(phase = State.Phase.END) }
        // the room stays for the next game
        room?.endGame()
        val match = _state.value.match
        if (daily) return saveDaily(match)
        val score = if (mode == GameMode.COOP) match.teamScore() else match.score(me.id)
        viewModelScope.launch {
            val previousBest = runCatching { leaderboardRepository.observe(GAME_ID, mode).first().firstOrNull { it.playerId == me.id }?.score }.getOrNull()
            val isRecord = leaderboardSync.submit(Record(me.id, me.name, GAME_ID, mode, score, System.currentTimeMillis()))
            _state.update { it.copy(newRecord = isRecord && score > 0, previousBest = previousBest) }
        }
    }

    /** The day is played: the result and the streak stay on the phone, the record goes to the players nearby */
    private fun saveDaily(match: ShowMatch) {
        val result = dailyResult(match)
        val streak = dailyStore.save(result)
        _state.update { it.copy(streak = streak.current(today)) }
        viewModelScope.launch {
            leaderboardSync.submit(Record(me.id, me.name, DailyPlan.gameId(today), GameMode.SOLO, result.points, System.currentTimeMillis()))
        }
    }

    /** Today's challenge is already played: straight to its result */
    private fun showPlayed(result: DailyResult) {
        val answers = result.answers.withIndex().associate { (index, answer) -> index to mapOf(me.id to answer) }
        _state.update {
            it.copy(
                phase = State.Phase.END,
                match = it.match.copy(answers = answers, round = rounds.lastIndex, isOver = true),
                streak = dailyStore.streak().current(today),
            )
        }
    }

    private fun observeNearby() {
        viewModelScope.launch {
            leaderboardRepository.observe(DailyPlan.gameId(today), GameMode.SOLO)
                .catch { emit(emptyList()) }
                .collect { records -> _state.update { it.copy(nearby = records) } }
        }
    }

    private fun dailyResult(match: ShowMatch) = DailyResult(today, rounds.map { match.answerOf(me.id, it.index) ?: RoundAnswer(correct = false, points = 0) })

    override fun onCleared() {
        voice.release()
        val room = room ?: return
        if (_state.value.phase != State.Phase.END) GameChannel.quit(room)
        room.endGame()
    }

    private fun Player.toShowPlayer() = ShowPlayer(id, name)

    private fun fuseMs(round: ShowRound, question: ShowQuestion): Long = when {
        round.final -> FINAL_FUSE_MS
        question is ShowQuestion.Build -> BUILD_FUSE_MS
        question is ShowQuestion.Compare -> COMPARE_FUSE_MS
        question.kind == ShowKind.VOICE -> VOICE_FUSE_MS
        else -> FUSE_MS
    }

    @Immutable
    internal data class State(
        val mode: GameMode,
        val meId: String,
        val phase: Phase,
        /** Seconds of the common countdown before the first round, 0 - the show goes */
        val countdown: Int,
        val match: ShowMatch,
        val question: ShowQuestion?,
        val loadFailed: Boolean,
        val fuse: Fuse?,
        /** The final after «Я первый»: the time to pick the answer */
        val answerFuse: Fuse?,
        val buzzShare: Float?,
        val bet: Boolean,
        val betUsed: Boolean,
        val fiftyUsed: Boolean,
        val freezeUsed: Boolean,
        /** Answers taken away by 50 на 50 */
        val hidden: Set<Int>,
        val picked: Int?,
        val tiles: Set<Int>,
        val myAnswer: RoundAnswer?,
        val voice: Voice,
        val replaysLeft: Int,
        val newRecord: Boolean,
        /** The best result before this show, null - the first one */
        val previousBest: Int?,
        val daily: Boolean,
        val date: LocalDate,
        /** Days in a row with the daily challenge, with today */
        val streak: Int,
        /** Today's results of the daily: mine and of the players met nearby */
        val nearby: List<Record>,
    ) {
        enum class Phase { INTRO, QUESTION, REVEAL, END }

        /** The answers can be picked: the question is open, and in the final - after the buzz */
        val canAnswer: Boolean get() = phase == Phase.QUESTION && myAnswer == null && (!match.current.final || buzzShare != null)

        fun forRound() = copy(
            phase = Phase.INTRO,
            question = null,
            fuse = null,
            answerFuse = null,
            buzzShare = null,
            bet = false,
            hidden = emptySet(),
            picked = null,
            tiles = emptySet(),
            myAnswer = null,
            voice = Voice.NONE,
            replaysLeft = 1,
        )

        companion object {
            fun start(mode: GameMode, meId: String, match: ShowMatch, daily: Boolean = false, date: LocalDate = LocalDate.now()) = State(
                mode = mode,
                meId = meId,
                phase = Phase.INTRO,
                countdown = 0,
                match = match,
                question = null,
                loadFailed = false,
                fuse = null,
                answerFuse = null,
                buzzShare = null,
                bet = false,
                betUsed = false,
                fiftyUsed = false,
                freezeUsed = false,
                hidden = emptySet(),
                picked = null,
                tiles = emptySet(),
                myAnswer = null,
                voice = Voice.NONE,
                replaysLeft = 1,
                newRecord = false,
                previousBest = null,
                daily = daily,
                date = date,
                streak = 0,
                nearby = emptyList(),
            )
        }
    }

    companion object {
        const val GAME_ID = "show"
        private const val COUNTDOWN_MS = 3_000L
        private const val INTRO_MS = 2_400L
        private const val DAILY_INTRO_MS = 1_200L
        private const val REVEAL_MS = 1_800L
        private const val VOICE_WAIT_MS = 5_000L
        private const val TICK_MS = 100L
        private const val MAX_ATTEMPTS = 20
        private const val FUSE_MS = 10_000L
        private const val VOICE_FUSE_MS = 12_000L
        private const val COMPARE_FUSE_MS = 8_000L
        private const val BUILD_FUSE_MS = 20_000L
        private const val FINAL_FUSE_MS = 12_000L
        private const val FINAL_ANSWER_MS = 4_000L
    }
}
