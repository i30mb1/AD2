package n7.ad2.games.internal.games.show

import kotlin.random.Random

/** A hero's voice line from the hero's responses */
internal data class VoiceLine(val text: String, val audioUrl: String)

private const val MIN_NAME_WORD = 3
private val SMALL_WORDS = setOf("the", "and", "of")

/** Four mana costs: the right one and three wrong ones off by 10..30, all above zero, shuffled */
internal fun manaOptions(right: Int, random: Random): List<Int> {
    val wrong = (-3..3).filter { it != 0 }.map { right + it * 10 }.filter { it > 0 }.shuffled(random).take(3)
    return (wrong + right).shuffled(random)
}

/** Lines that do not give the hero away by the name, have a sound and a text to show after the answer */
internal fun guessableLines(heroName: String, lines: List<VoiceLine>): List<VoiceLine> {
    val words = heroName.lowercase().split(' ', '-', '\'').filter { it.length >= MIN_NAME_WORD && it !in SMALL_WORDS }
    return lines.filter { line ->
        val text = line.text.lowercase()
        line.audioUrl.isNotBlank() && text.isNotBlank() && words.none { it in text }
    }
}
