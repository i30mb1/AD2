package n7.ad2.games.internal.games.show

import java.time.LocalDate
import java.time.format.DateTimeParseException

/** The daily challenge: the same questions for everyone on a day */
internal object DailyPlan {
    const val QUESTIONS = 10
    private const val GAME_PREFIX = "daily-"
    private const val SEED_STEP = 7_919L
    private const val SEED_SALT = 104_729L

    /** The kinds in the order of the day, two questions each */
    private val KINDS = listOf(ShowKind.MANA, ShowKind.VOICE, ShowKind.BUILD, ShowKind.COMPARE, ShowKind.HERO)

    fun create(): List<ShowRound> = KINDS.flatMap { listOf(it, it) }.mapIndexed { index, kind -> ShowRound(index, kind, final = false) }

    fun seed(date: LocalDate): Long = date.toEpochDay() * SEED_STEP + SEED_SALT

    /** Records of the day: the records sync with the players nearby, so they see each other's result */
    fun gameId(date: LocalDate): String = "$GAME_PREFIX$date"
}

/** Days in a row with the daily played: [lastDay] - the last of them */
internal data class DailyStreak(val lastDay: LocalDate? = null, val days: Int = 0) {

    fun played(today: LocalDate): DailyStreak = when (lastDay) {
        today -> this
        today.minusDays(1) -> DailyStreak(today, days + 1)
        else -> DailyStreak(today, 1)
    }

    /** The streak as it is today: it is still alive the day after the last game */
    fun current(today: LocalDate): Int = if (lastDay == today || lastDay == today.minusDays(1)) days else 0
}

/** The result of a day: an answer per question */
internal data class DailyResult(val date: LocalDate, val answers: List<RoundAnswer>) {
    val correct: Int get() = answers.count { it.correct }
    val points: Int get() = answers.sumOf { it.points }
}

/** A result as "2026-11-01;1:180,0:0" */
internal object DailyCodec {

    fun encode(result: DailyResult): String = "${result.date};" + result.answers.joinToString(",") { "${if (it.correct) 1 else 0}:${it.points}" }

    fun decode(text: String): DailyResult? {
        val parts = text.split(";")
        if (parts.size != 2) return null
        val date = try {
            LocalDate.parse(parts[0])
        } catch (e: DateTimeParseException) {
            return null
        }
        val answers = parts[1].split(",").filter { it.isNotEmpty() }.map { item ->
            val fields = item.split(":")
            if (fields.size != 2) return null
            RoundAnswer(correct = fields[0] == "1", points = fields[1].toIntOrNull() ?: return null)
        }
        return DailyResult(date, answers)
    }
}

/** The result for a chat: the title, X/10 with the points and a square per question, five in a row */
internal fun dailyShareText(title: String, result: DailyResult): String {
    val squares = result.answers.map { if (it.correct) "🟩" else "🟥" }.chunked(5).joinToString("\n") { it.joinToString("") }
    return "$title\n${result.correct}/${result.answers.size} · ${points(result.points)}\n$squares"
}
