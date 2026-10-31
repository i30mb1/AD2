package n7.ad2.games.internal.games.show

internal data class ShowPlayer(val id: String, val name: String)

/** [points] are counted by the player who answered: speed, the bet and the final are in them */
internal data class RoundAnswer(val correct: Boolean, val points: Int)

/**
 * Everyone gets the same questions; a round is complete when every player still here answered (a burned fuse is an answer too).
 * Answers to the next round may come before it: they wait in [answers]. Points of a round show once it is complete.
 */
internal data class ShowMatch(
    val players: List<ShowPlayer>,
    val rounds: List<ShowRound>,
    val round: Int = 0,
    val answers: Map<Int, Map<String, RoundAnswer>> = emptyMap(),
    val gone: Set<String> = emptySet(),
    val isOver: Boolean = false,
) {

    private val present: List<ShowPlayer> get() = players.filter { it.id !in gone }

    val current: ShowRound get() = rounds[round]

    val roundComplete: Boolean get() = present.all { answers[round]?.containsKey(it.id) == true }

    val peerLeft: Boolean get() = gone.isNotEmpty()

    fun score(playerId: String): Int = answers
        .filterKeys { it < round || it == round && (roundComplete || isOver) }
        .values
        .sumOf { it[playerId]?.points ?: 0 }

    fun teamScore(): Int = players.sumOf { score(it.id) }

    fun answerOf(playerId: String, round: Int = this.round): RoundAnswer? = answers[round]?.get(playerId)

    fun answer(playerId: String, round: Int, answer: RoundAnswer): ShowMatch {
        if (isOver || round < this.round || round >= rounds.size) return this
        val inRound = answers[round].orEmpty()
        if (playerId in inRound) return this
        return copy(answers = answers + (round to inRound + (playerId to answer)))
    }

    fun nextRound(): ShowMatch = when {
        isOver -> this
        round + 1 >= rounds.size -> copy(isOver = true)
        else -> copy(round = round + 1)
    }

    /** Alone the show makes no sense: when nobody else is left, it is over */
    fun leave(playerId: String): ShowMatch {
        if (playerId in gone || players.none { it.id == playerId }) return this
        val left = copy(gone = gone + playerId)
        return if (left.present.size <= 1) left.copy(isOver = true) else left
    }

    /** Players by score, the best first */
    fun standings(): List<ShowPlayer> = players.sortedByDescending { score(it.id) }
}

/** An answer of another player as "a;round;correct;points" */
internal object ShowCodec {
    private const val ANSWER = "a"

    fun encode(round: Int, answer: RoundAnswer): String = "$ANSWER;$round;${answer.correct};${answer.points}"

    fun decode(payload: String): Pair<Int, RoundAnswer>? {
        val parts = payload.split(";")
        if (parts.size != 4 || parts[0] != ANSWER) return null
        val round = parts[1].toIntOrNull() ?: return null
        val correct = parts[2].toBooleanStrictOrNull() ?: return null
        val points = parts[3].toIntOrNull() ?: return null
        return round to RoundAnswer(correct, points)
    }
}
