import com.google.common.truth.Truth
import kotlin.random.Random
import n7.ad2.games.internal.games.show.Fuse
import n7.ad2.games.internal.games.show.ShowKind
import n7.ad2.games.internal.games.show.ShowPlan
import n7.ad2.games.internal.games.show.ShowScoring
import n7.ad2.games.internal.games.show.fiftyFifty
import org.junit.Test

internal class ShowRulesTest {

    @Test
    fun `a show is every kind once and then the final of a kind with four answers`() {
        val plan = ShowPlan.create(seed = 7)

        Truth.assertThat(plan).hasSize(ShowPlan.ROUNDS)
        Truth.assertThat(plan.take(5).map { it.kind }).containsExactlyElementsIn(ShowKind.entries)
        Truth.assertThat(plan.map { it.final }).containsExactly(false, false, false, false, false, true).inOrder()
        Truth.assertThat(plan.last().kind).isIn(ShowPlan.FINAL_KINDS)
        Truth.assertThat(plan.map { it.index }).containsExactly(0, 1, 2, 3, 4, 5).inOrder()
    }

    @Test
    fun `the final never repeats the kind of the round before it`() {
        repeat(200) { seed ->
            val plan = ShowPlan.create(seed.toLong())
            Truth.assertThat(plan[5].kind).isNotEqualTo(plan[4].kind)
        }
    }

    @Test
    fun `the same seed gives the same show on every phone, other seeds mix the order`() {
        Truth.assertThat(ShowPlan.create(42)).isEqualTo(ShowPlan.create(42))
        val orders = (0L until 30L).map { seed -> ShowPlan.create(seed).map { it.kind } }.toSet()
        Truth.assertThat(orders.size).isGreaterThan(10)
    }

    @Test
    fun `the fuse burns down to zero`() {
        val fuse = Fuse(startedAt = 1_000, lengthMs = 10_000)

        Truth.assertThat(fuse.left(1_000)).isEqualTo(10_000)
        Truth.assertThat(fuse.left(6_000)).isEqualTo(5_000)
        Truth.assertThat(fuse.share(6_000)).isWithin(0.001f).of(0.5f)
        Truth.assertThat(fuse.left(30_000)).isEqualTo(0)
    }

    @Test
    fun `a freeze stops the fuse for five seconds, once`() {
        val fuse = Fuse(startedAt = 0, lengthMs = 10_000).freeze(now = 2_000)

        Truth.assertThat(fuse.left(4_000)).isEqualTo(8_000)
        Truth.assertThat(fuse.isFrozen(4_000)).isTrue()
        Truth.assertThat(fuse.left(7_000)).isEqualTo(8_000)
        Truth.assertThat(fuse.isFrozen(7_000)).isFalse()
        Truth.assertThat(fuse.left(9_000)).isEqualTo(6_000)
        Truth.assertThat(fuse.freeze(now = 9_000)).isEqualTo(fuse)
    }

    @Test
    fun `a right answer gives 100 and up to 100 more for speed, a bet doubles it`() {
        Truth.assertThat(ShowScoring.round(correct = true, share = 1f, bet = false)).isEqualTo(200)
        Truth.assertThat(ShowScoring.round(correct = true, share = 0.5f, bet = false)).isEqualTo(150)
        Truth.assertThat(ShowScoring.round(correct = true, share = 0f, bet = false)).isEqualTo(100)
        Truth.assertThat(ShowScoring.round(correct = true, share = 0.5f, bet = true)).isEqualTo(300)
    }

    @Test
    fun `a wrong answer gives nothing, with a bet it takes 100`() {
        Truth.assertThat(ShowScoring.round(correct = false, share = 0.9f, bet = false)).isEqualTo(0)
        Truth.assertThat(ShowScoring.round(correct = false, share = 0.9f, bet = true)).isEqualTo(-100)
    }

    @Test
    fun `in the final the earlier the buzz the more points, a miss after the buzz takes 100, no buzz gives nothing`() {
        Truth.assertThat(ShowScoring.final(correct = true, buzzShare = 1f)).isEqualTo(400)
        Truth.assertThat(ShowScoring.final(correct = true, buzzShare = 0.25f)).isEqualTo(250)
        Truth.assertThat(ShowScoring.final(correct = false, buzzShare = 0.8f)).isEqualTo(-100)
        Truth.assertThat(ShowScoring.final(correct = false, buzzShare = null)).isEqualTo(0)
    }

    @Test
    fun `fifty-fifty hides two wrong answers and never the right one`() {
        repeat(50) { seed ->
            val right = seed % 4
            val hidden = fiftyFifty(rightIndex = right, count = 4, random = Random(seed))

            Truth.assertThat(hidden).hasSize(2)
            Truth.assertThat(hidden).doesNotContain(right)
            Truth.assertThat(hidden.all { it in 0..3 }).isTrue()
        }
    }
}
