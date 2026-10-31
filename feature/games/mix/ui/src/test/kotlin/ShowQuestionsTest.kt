import com.google.common.truth.Truth
import kotlin.math.abs
import kotlin.random.Random
import n7.ad2.games.internal.games.show.VoiceLine
import n7.ad2.games.internal.games.show.guessableLines
import n7.ad2.games.internal.games.show.manaOptions
import org.junit.Test

internal class ShowQuestionsTest {

    @Test
    fun `four mana costs - the right one and three wrong ones off by tens, all above zero`() {
        repeat(100) { seed ->
            val right = listOf(5, 10, 25, 75, 100, 150)[seed % 6]
            val options = manaOptions(right, Random(seed))

            Truth.assertThat(options).hasSize(4)
            Truth.assertThat(options.toSet()).hasSize(4)
            Truth.assertThat(options).contains(right)
            options.forEach { option ->
                Truth.assertThat(option).isGreaterThan(0)
                Truth.assertThat(abs(option - right) % 10).isEqualTo(0)
                Truth.assertThat(abs(option - right)).isAtMost(30)
            }
        }
    }

    @Test
    fun `a voice line with the hero name or without a sound is not asked`() {
        val lines = listOf(
            VoiceLine("Axe is all you need!", "a.mp3"),
            VoiceLine("Enemies need killing!", "b.mp3"),
            VoiceLine("Come and get it!", ""),
        )

        Truth.assertThat(guessableLines("Axe", lines)).containsExactly(VoiceLine("Enemies need killing!", "b.mp3"))
    }

    @Test
    fun `a voice line without text is not asked - after the answer there would be nothing to show`() {
        val lines = listOf(VoiceLine("", "a.mp3"), VoiceLine("  ", "b.mp3"), VoiceLine("Ha!", "c.mp3"))

        Truth.assertThat(guessableLines("Axe", lines).map { it.text }).containsExactly("Ha!")
    }

    @Test
    fun `every word of a long hero name gives the hero away`() {
        val lines = listOf(VoiceLine("The mage falls!", "a.mp3"), VoiceLine("Magic is an abomination.", "b.mp3"))

        Truth.assertThat(guessableLines("Anti-Mage", lines).map { it.text }).containsExactly("Magic is an abomination.")
    }

    @Test
    fun `small words of a name like the and of do not give the hero away`() {
        val lines = listOf(VoiceLine("Into the light!", "a.mp3"), VoiceLine("Fear the dark.", "b.mp3"))

        Truth.assertThat(guessableLines("Keeper of the Light", lines).map { it.text }).containsExactly("Fear the dark.")
    }
}
