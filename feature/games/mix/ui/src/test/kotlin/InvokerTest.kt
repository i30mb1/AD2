import com.google.common.truth.Truth
import n7.ad2.games.internal.games.invoker.InvokerCodec
import n7.ad2.games.internal.games.invoker.InvokerRun
import n7.ad2.games.internal.games.invoker.InvokerSpell
import n7.ad2.games.internal.games.invoker.Orb
import n7.ad2.games.internal.games.invoker.OrbsDiff
import n7.ad2.games.internal.games.invoker.diff
import n7.ad2.games.internal.games.invoker.push
import n7.ad2.games.internal.games.invoker.spellQueue
import org.junit.Test

internal class InvokerTest {

    private val q = Orb.QUAS
    private val w = Orb.WEX
    private val e = Orb.EXORT

    @Test
    fun `three orbs make a spell whatever their order, the ten spells are all different`() {
        Truth.assertThat(InvokerSpell.of(listOf(e, q, w))).isEqualTo(InvokerSpell.DEAFENING_BLAST)
        Truth.assertThat(InvokerSpell.of(listOf(w, e, e))).isEqualTo(InvokerSpell.CHAOS_METEOR)
        Truth.assertThat(InvokerSpell.of(listOf(e, e))).isNull()
        Truth.assertThat(InvokerSpell.entries.map { it.orbs.sorted() }.toSet()).hasSize(10)
    }

    @Test
    fun `a new orb pushes out the oldest`() {
        val orbs = emptyList<Orb>().push(q).push(w).push(e).push(e)

        Truth.assertThat(orbs).containsExactly(w, e, e).inOrder()
    }

    @Test
    fun `the diff tells which orbs are missing and which ones are extra`() {
        Truth.assertThat(diff(listOf(e, e, w), InvokerSpell.SUN_STRIKE)).isEqualTo(OrbsDiff(missing = listOf(e), extra = setOf(2)))
        Truth.assertThat(diff(listOf(q, q, q), InvokerSpell.DEAFENING_BLAST)).isEqualTo(OrbsDiff(missing = listOf(w, e), extra = setOf(1, 2)))
        Truth.assertThat(diff(listOf(q), InvokerSpell.COLD_SNAP)).isEqualTo(OrbsDiff(missing = listOf(q, q), extra = emptySet()))
    }

    @Test
    fun `the queue never asks the same spell twice in a row and is the same for the same seed`() {
        val queue = spellQueue(seed = 5)

        Truth.assertThat(queue.zipWithNext().none { (a, b) -> a == b }).isTrue()
        Truth.assertThat(queue).isEqualTo(spellQueue(seed = 5))
        Truth.assertThat(queue).isNotEqualTo(spellQueue(seed = 6))
        Truth.assertThat(queue.toSet()).hasSize(10)
    }

    @Test
    fun `a right invoke scores 100 and 10 more for each spell of the streak, keeps the orbs and asks the next spell`() {
        var run = InvokerRun(listOf(InvokerSpell.SUN_STRIKE, InvokerSpell.COLD_SNAP) + spellQueue(seed = 1)).press(e).press(e).press(e)

        run = run.invoke()
        Truth.assertThat(run.score).isEqualTo(100)
        Truth.assertThat(run.streak).isEqualTo(1)
        Truth.assertThat(run.target).isEqualTo(InvokerSpell.COLD_SNAP)
        Truth.assertThat(run.orbs).containsExactly(e, e, e)
        Truth.assertThat(run.lastPoints).isEqualTo(100)

        run = run.press(q).press(q).press(q).invoke()
        Truth.assertThat(run.score).isEqualTo(210)
        Truth.assertThat(run.invoked).isEqualTo(2)
    }

    @Test
    fun `the streak bonus stops at double points`() {
        val run = InvokerRun(List(30) { InvokerSpell.EMP }, streak = 25).press(w).press(w).press(w).invoke()

        Truth.assertThat(run.lastPoints).isEqualTo(200)
    }

    @Test
    fun `a wrong invoke breaks the streak and tells what is off, the next orb clears it`() {
        var run = InvokerRun(listOf(InvokerSpell.SUN_STRIKE, InvokerSpell.EMP), streak = 4, bestStreak = 4).press(e).press(e).press(w)

        run = run.invoke()
        Truth.assertThat(run.streak).isEqualTo(0)
        Truth.assertThat(run.bestStreak).isEqualTo(4)
        Truth.assertThat(run.mistakes).isEqualTo(1)
        Truth.assertThat(run.wrong).isEqualTo(OrbsDiff(missing = listOf(e), extra = setOf(2)))
        Truth.assertThat(run.target).isEqualTo(InvokerSpell.SUN_STRIKE)

        Truth.assertThat(run.press(e).wrong).isNull()
    }

    @Test
    fun `the hint costs half the points of the spell`() {
        val run = InvokerRun(listOf(InvokerSpell.SUN_STRIKE, InvokerSpell.EMP), streak = 5).hint().press(e).press(e).press(e).invoke()

        Truth.assertThat(run.lastPoints).isEqualTo(75)
        Truth.assertThat(run.hinted).isFalse()
    }

    @Test
    fun `the score goes over the network and back`() {
        Truth.assertThat(InvokerCodec.decode(InvokerCodec.encode(score = 1_350, invoked = 9))).isEqualTo(1_350 to 9)
        Truth.assertThat(InvokerCodec.decode("x;1;2")).isNull()
    }
}
