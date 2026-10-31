package n7.ad2.games.internal.games.mid

import android.os.SystemClock
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import n7.ad2.games.internal.ActiveRoom
import n7.ad2.games.internal.MatchClock
import n7.ad2.games.internal.party.GameChannel
import n7.ad2.games.internal.party.Pick
import n7.ad2.games.leaderboard.LeaderboardRepository
import n7.ad2.games.leaderboard.LeaderboardSync
import n7.ad2.games.leaderboard.LocalPlayerProvider
import n7.ad2.games.leaderboard.Record
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Room
import n7.ad2.games.room.RoomState

/** An attack of a hero at a creep: the swing until [releaseAt] can still be canceled, then the hit is on its way */
internal data class Attack(val creepId: Int, val startAt: Long, val releaseAt: Long, val landAt: Long)

/** Text over the field next to a creep: my gold, deny or miss, or [who] else took it */
internal data class MidFloat(val id: Long, val point: LanePoint, val kind: Kind, val gold: Int = 0, val who: String? = null) {
    enum class Kind { GOLD, DENY, MISS, OTHER_GOLD, OTHER_DENY }

    companion object {
        /** How long a float stays over the field, rising and fading out */
        const val LIFE_MS = 900L
    }
}

/** What happened to my last hit, shown under the field: the timing is what the game teaches */
internal sealed interface Feedback {
    data class LastHit(val gold: Int) : Feedback
    data object Deny : Feedback
    data class Early(val byMs: Long, val hp: Int) : Feedback
    data class Late(val byMs: Long) : Feedback
    data class Taken(val by: String) : Feedback
    data object Canceled : Feedback
}

/** My hits: [errorMs] adds up how far early or late the missed ones were */
internal data class MidStats(val attacks: Int = 0, val taken: Int = 0, val early: Int = 0, val late: Int = 0, val errorMs: Long = 0) {
    val accuracy: Int get() = if (attacks == 0) 0 else taken * 100 / attacks
    val averageErrorMs: Long get() = if (early + late == 0) 0 else errorMs / (early + late)
}

/**
 * The mid lane game. Solo: against a bot or in training without one. Versus: the host plays Radiant, the guest Dire,
 * each phone decides about its own hits and tells the other one. Together: both play Radiant against a bot.
 */
internal class MidViewModel @AssistedInject constructor(
    @Assisted private val mode: GameMode,
    private val getMidHeroesUseCase: GetMidHeroesUseCase,
    private val leaderboardSync: LeaderboardSync,
    private val leaderboardRepository: LeaderboardRepository,
    localPlayerProvider: LocalPlayerProvider,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(mode: GameMode): MidViewModel
    }

    private val room: Room? = ActiveRoom.room.takeIf { mode != GameMode.SOLO }
    private val playing = room?.state?.value as? RoomState.Playing
    private val me = room?.me ?: localPlayerProvider.localPlayer()
    private val isHost = playing == null || playing.host.id == me.id
    private val peerName = playing?.let { if (isHost) it.guest.name else it.host.name }
    private val mySide = if (mode == GameMode.VERSUS && !isHost) Side.DIRE else Side.RADIANT
    private val peerSide = if (mode == GameMode.VERSUS) mySide.enemy else mySide
    private val seed = playing?.seed ?: Random.nextLong()

    /** The lane is the same on both phones: the room's seed, a random one in solo */
    val sim = LaneSim(seed)

    private var clock: MatchClock? = null
    private var nextAttackAt = Long.MIN_VALUE
    private val resolvedBotAttacks = mutableSetOf<Int>()
    private var floatIds = 0L

    private val _state = MutableStateFlow(State.start(mode, mySide, peerName))
    val state: StateFlow<State> = _state.asStateFlow()

    /** The result screen is closed («Again» or «Another game»): back to the games list */
    private val _exit = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val exit: SharedFlow<Unit> = _exit.asSharedFlow()

    init {
        viewModelScope.launch {
            val heroes = runCatching { getMidHeroesUseCase() }.getOrDefault(emptyList())
            _state.update { it.copy(heroes = heroes, myHero = it.myHero ?: heroes.firstOrNull()) }
        }
        room?.let(::observeRoom)
    }

    fun elapsedMs(): Long = clock?.elapsed(SystemClock.elapsedRealtime()) ?: Long.MIN_VALUE

    fun countdown(): Int = clock?.countdown(SystemClock.elapsedRealtime()) ?: 0

    fun pickHero(hero: MidHero) {
        if (_state.value.myReady) return
        _state.update { it.copy(myHero = hero) }
    }

    fun pickLevel(level: BotLevel) = _state.update { it.copy(level = level, training = false) }

    fun pickTraining() = _state.update { it.copy(training = true) }

    /** Solo starts right away; over the network the host starts once both are ready */
    fun ready() {
        val hero = _state.value.myHero ?: return
        if (room == null) return start()
        if (_state.value.myReady) return
        _state.update { it.copy(myReady = true) }
        send(MidCodec.ready(hero.key))
        startIfBothReady()
    }

    /** A tap on a creep starts a swing at it; during a swing it switches the target, a released hit cannot be taken back */
    fun onCreepTap(creepId: Int) {
        val state = _state.value
        val hero = state.myHero ?: return
        val now = elapsedMs()
        if (now < 0 || state.match.isOver) return
        val creep = sim.creep(creepId)
        if (!sim.isAlive(creep, now) || state.match.isTaken(creepId)) return
        val current = state.myAttack
        val swinging = current != null && now < current.releaseAt
        if (!swinging && now < nextAttackAt) return
        val distance = LaneSim.distance(LaneSim.heroPoint(mySide), sim.positionAt(creep, now + hero.attackPointMs))
        val attack = Attack(creepId, startAt = now, releaseAt = now + hero.attackPointMs, landAt = hero.landAt(now, distance))
        nextAttackAt = now + hero.attackRateMs
        _state.update { it.copy(myAttack = attack, stats = if (swinging) it.stats else it.stats.copy(attacks = it.stats.attacks + 1)) }
        send(MidCodec.swing(creepId, now))
    }

    /** A tap on my hero stops the swing, like a stop command in the game: no attack is spent */
    fun onHeroTap() {
        val attack = _state.value.myAttack ?: return
        val now = elapsedMs()
        if (now >= attack.releaseAt) return
        nextAttackAt = now
        _state.update { it.copy(myAttack = null, feedback = Feedback.Canceled, stats = it.stats.copy(attacks = it.stats.attacks - 1)) }
    }

    /** The same game and mode: together the games list picks it again, solo opens it anew */
    fun again() {
        ActiveRoom.requestRematch(Pick(GAME_ID, mode))
        _exit.tryEmit(Unit)
    }

    fun otherGame() {
        _exit.tryEmit(Unit)
    }

    private fun startIfBothReady() {
        val state = _state.value
        if (isHost && state.myReady && state.peerReady && clock == null) {
            send(MidCodec.go())
            start()
        }
    }

    private fun start() {
        if (clock != null) return
        val state = _state.value
        val heroes = state.heroes.ifEmpty { return }
        val withBot = mode == GameMode.COOP || (mode == GameMode.SOLO && !state.training)
        val botHero = if (withBot) heroes[Random(seed).nextInt(heroes.size)] else null
        val level = if (mode == GameMode.COOP) BotLevel.MEDIUM else state.level
        val plan = botHero?.let { planBot(sim, mySide.enemy, it, level, seed) }.orEmpty()
        clock = MatchClock(SystemClock.elapsedRealtime(), COUNTDOWN_MS)
        _state.update { it.copy(phase = State.Phase.PLAYING, botHero = botHero, botPlan = plan) }
        viewModelScope.launch { tick() }
    }

    /** Hits land in time order: mine and the bot's, whichever came first takes the creep */
    private suspend fun tick() {
        while (!_state.value.match.isOver) {
            val now = elapsedMs()
            val due = mutableListOf<Pair<Long, () -> Unit>>()
            _state.value.myAttack?.takeIf { it.landAt <= now }?.let { attack -> due += attack.landAt to { resolveMine(attack) } }
            _state.value.botPlan.forEachIndexed { index, plan ->
                if (plan.landAt <= now && resolvedBotAttacks.add(index)) due += plan.landAt to { resolveBot(plan) }
            }
            due.sortedBy { it.first }.forEach { (_, resolve) -> resolve() }
            if (_state.value.peerSwings.any { it.landAt < now }) _state.update { state -> state.copy(peerSwings = state.peerSwings.filter { it.landAt >= now }) }
            if (now >= sim.endsAt) update { it.end() }
            delay(TICK_MS)
        }
    }

    private fun resolveMine(attack: Attack) {
        _state.update { it.copy(myAttack = null) }
        val state = _state.value
        val hero = state.myHero ?: return
        val creep = sim.creep(attack.creepId)
        val point = sim.positionAt(creep, attack.landAt)
        when (val outcome = sim.outcome(creep, hero, attack.landAt)) {
            Outcome.Kill -> if (state.match.takenBefore(creep.id, attack.landAt)) {
                val by = opponentName(state.match.claims.getValue(creep.id).side)
                _state.update { it.copy(feedback = Feedback.Taken(by), stats = it.stats.copy(late = it.stats.late + 1)) }
            } else {
                update { it.claim(creep, mySide, attack.landAt) }
                send(MidCodec.claim(creep.id, attack.landAt))
                val deny = creep.side == mySide
                float(point, if (deny) MidFloat.Kind.DENY else MidFloat.Kind.GOLD, gold = creep.type.gold)
                _state.update { it.copy(feedback = if (deny) Feedback.Deny else Feedback.LastHit(creep.type.gold), stats = it.stats.copy(taken = it.stats.taken + 1)) }
            }
            is Outcome.Early -> {
                float(point, MidFloat.Kind.MISS)
                _state.update { it.copy(feedback = Feedback.Early(outcome.byMs, outcome.hp), stats = it.stats.copy(early = it.stats.early + 1, errorMs = it.stats.errorMs + outcome.byMs)) }
            }
            is Outcome.Late -> {
                float(point, MidFloat.Kind.MISS)
                _state.update { it.copy(feedback = Feedback.Late(outcome.byMs), stats = it.stats.copy(late = it.stats.late + 1, errorMs = it.stats.errorMs + outcome.byMs)) }
            }
        }
    }

    private fun resolveBot(plan: PlannedAttack) {
        val state = _state.value
        val hero = state.botHero ?: return
        val creep = sim.creep(plan.creepId)
        if (sim.outcome(creep, hero, plan.landAt) != Outcome.Kill || state.match.takenBefore(creep.id, plan.landAt)) return
        update { it.claim(creep, mySide.enemy, plan.landAt) }
        opponentFloat(creep, mySide.enemy)
    }

    private fun observeRoom(room: Room) {
        viewModelScope.launch {
            room.events.collect { event ->
                when (val message = MidCodec.decode(event.payload)) {
                    is MidCodec.Message.Ready -> {
                        _state.update { it.copy(peerHeroKey = message.heroKey, peerReady = true) }
                        startIfBothReady()
                    }
                    MidCodec.Message.Go -> start()
                    is MidCodec.Message.Claim -> peerClaim(message)
                    is MidCodec.Message.Swing -> peerSwing(message)
                    null -> Unit
                }
            }
        }
        viewModelScope.launch {
            room.state.first { it is RoomState.Closed }
            if (!_state.value.match.isOver) update { it.peerLeft() }
        }
        viewModelScope.launch {
            room.syncEvents.first(GameChannel::isQuit)
            if (!_state.value.match.isOver) update { it.peerLeft() }
        }
    }

    private fun peerClaim(message: MidCodec.Message.Claim) {
        val creep = sim.creeps.getOrNull(message.creepId) ?: return
        val wasMine = _state.value.match.claims[creep.id]?.side == mySide && mode == GameMode.VERSUS
        update { it.claim(creep, peerSide, message.landAt) }
        val winner = _state.value.match.claims[creep.id]?.side ?: return
        if (mode == GameMode.VERSUS && winner == mySide) return
        opponentFloat(creep, peerSide)
        // the other hit landed earlier than mine: the creep goes to them
        if (wasMine) _state.update { it.copy(feedback = Feedback.Taken(peerName.orEmpty())) }
    }

    private fun peerSwing(message: MidCodec.Message.Swing) {
        val hero = _state.value.peerHero ?: return
        val creep = sim.creeps.getOrNull(message.creepId) ?: return
        val distance = LaneSim.distance(LaneSim.heroPoint(peerSide), sim.positionAt(creep, message.startAt + hero.attackPointMs))
        val swing = Attack(creep.id, message.startAt, message.startAt + hero.attackPointMs, hero.landAt(message.startAt, distance))
        _state.update { it.copy(peerSwings = it.peerSwings + swing) }
    }

    private fun opponentFloat(creep: Creep, side: Side) {
        val kind = if (creep.side == side) MidFloat.Kind.OTHER_DENY else MidFloat.Kind.OTHER_GOLD
        float(creep.slot, kind, gold = creep.type.gold, who = opponentName(side))
    }

    /** Who took a creep from me: the other player, or the bot on the enemy side */
    private fun opponentName(side: Side): String = when {
        mode != GameMode.SOLO && side == peerSide -> peerName.orEmpty()
        else -> _state.value.botHero?.name.orEmpty()
    }

    private fun update(change: (MidMatch) -> MidMatch) {
        _state.update { it.copy(match = change(it.match)) }
        if (_state.value.match.isOver) finish()
    }

    private fun float(point: LanePoint, kind: MidFloat.Kind, gold: Int = 0, who: String? = null) {
        val item = MidFloat(floatIds++, point, kind, gold, who)
        _state.update { it.copy(floats = it.floats + item) }
        viewModelScope.launch {
            delay(MidFloat.LIFE_MS)
            _state.update { state -> state.copy(floats = state.floats.filterNot { it.id == item.id }) }
        }
    }

    private fun send(payload: String) {
        room?.let { runCatching { it.sendEvent(payload) } }
    }

    private fun finish() {
        if (_state.value.phase == State.Phase.END) return
        _state.update { it.copy(phase = State.Phase.END, myAttack = null) }
        // the room stays for the next game
        room?.endGame()
        val state = _state.value
        if (state.training) return
        val score = state.match.score(mySide)
        viewModelScope.launch {
            val previousBest = runCatching { leaderboardRepository.observe(GAME_ID, mode).first().firstOrNull { it.playerId == me.id }?.score }.getOrNull()
            val isRecord = leaderboardSync.submit(Record(me.id, me.name, GAME_ID, mode, score, System.currentTimeMillis()))
            _state.update { it.copy(newRecord = isRecord && score > 0, previousBest = previousBest) }
        }
    }

    override fun onCleared() {
        val room = room ?: return
        if (_state.value.phase != State.Phase.END) GameChannel.quit(room)
        room.endGame()
    }

    @Immutable
    internal data class State(
        val mode: GameMode,
        val phase: Phase,
        val heroes: List<MidHero>,
        val myHero: MidHero?,
        val level: BotLevel,
        val training: Boolean,
        val mySide: Side,
        val peerName: String?,
        val peerHeroKey: String?,
        val myReady: Boolean,
        val peerReady: Boolean,
        val botHero: MidHero?,
        val botPlan: List<PlannedAttack>,
        val match: MidMatch,
        val myAttack: Attack?,
        val peerSwings: List<Attack>,
        val floats: List<MidFloat>,
        val feedback: Feedback?,
        val stats: MidStats,
        val newRecord: Boolean,
        /** The best result before this game, null - the first game */
        val previousBest: Int?,
    ) {
        val peerHero: MidHero? get() = heroes.firstOrNull { it.key == peerHeroKey }
        val opponentSide: Side get() = mySide.enemy

        /** Who plays the other side: the other player in versus, otherwise the bot */
        val opponentHero: MidHero? get() = if (mode == GameMode.VERSUS) peerHero else botHero

        enum class Phase { PICK, PLAYING, END }

        companion object {
            fun start(mode: GameMode, mySide: Side, peerName: String?) = State(
                mode = mode,
                phase = Phase.PICK,
                heroes = emptyList(),
                myHero = null,
                level = BotLevel.MEDIUM,
                training = false,
                mySide = mySide,
                peerName = peerName,
                peerHeroKey = null,
                myReady = false,
                peerReady = false,
                botHero = null,
                botPlan = emptyList(),
                match = MidMatch(),
                myAttack = null,
                peerSwings = emptyList(),
                floats = emptyList(),
                feedback = null,
                stats = MidStats(),
                newRecord = false,
                previousBest = null,
            )
        }
    }

    companion object {
        const val GAME_ID = "mid"
        private const val COUNTDOWN_MS = 3_000L
        private const val TICK_MS = 16L
    }
}
