import com.google.common.truth.Truth
import n7.ad2.games.internal.games.mid.BotLevel
import n7.ad2.games.internal.games.mid.CreepType
import n7.ad2.games.internal.games.mid.LaneSim
import n7.ad2.games.internal.games.mid.MidCodec
import n7.ad2.games.internal.games.mid.MidHero
import n7.ad2.games.internal.games.mid.MidMatch
import n7.ad2.games.internal.games.mid.Outcome
import n7.ad2.games.internal.games.mid.Side
import n7.ad2.games.internal.games.mid.outcome
import n7.ad2.games.internal.games.mid.planBot
import org.junit.Test

internal class MidMatchTest {

    private val sim = LaneSim(seed = 21)
    private val direCreep = sim.creeps.first { it.side == Side.DIRE && it.type == CreepType.MELEE }
    private val radiantCreep = sim.creeps.first { it.side == Side.RADIANT && it.type == CreepType.RANGED }
    private val lina = MidHero("lina", "Lina", "", damage = 53, attackPointMs = 750, attackRateMs = 1_600, projectileSpeed = 1_000)

    @Test
    fun `an enemy creep is a last hit with its gold, an own one is a deny without gold`() {
        val match = MidMatch()
            .claim(direCreep, Side.RADIANT, landAt = 9_000)
            .claim(radiantCreep, Side.RADIANT, landAt = 9_500)

        Truth.assertThat(match.lastHits(Side.RADIANT)).isEqualTo(1)
        Truth.assertThat(match.denies(Side.RADIANT)).isEqualTo(1)
        Truth.assertThat(match.gold(Side.RADIANT)).isEqualTo(CreepType.MELEE.gold)
        Truth.assertThat(match.score(Side.RADIANT)).isEqualTo(2)
        Truth.assertThat(match.score(Side.DIRE)).isEqualTo(0)
    }

    @Test
    fun `the hit that landed first takes the creep, whatever came over the network first`() {
        val match = MidMatch()
            .claim(direCreep, Side.RADIANT, landAt = 9_200)
            .claim(direCreep, Side.DIRE, landAt = 9_100)

        Truth.assertThat(match.score(Side.RADIANT)).isEqualTo(0)
        Truth.assertThat(match.denies(Side.DIRE)).isEqualTo(1)
        Truth.assertThat(match.takenBefore(direCreep.id, 9_150)).isTrue()
        Truth.assertThat(match.takenBefore(direCreep.id, 9_050)).isFalse()
    }

    @Test
    fun `hits landing at the same moment go to Radiant, the host, on both phones`() {
        val oneOrder = MidMatch().claim(direCreep, Side.DIRE, landAt = 9_000).claim(direCreep, Side.RADIANT, landAt = 9_000)
        val otherOrder = MidMatch().claim(direCreep, Side.RADIANT, landAt = 9_000).claim(direCreep, Side.DIRE, landAt = 9_000)
        Truth.assertThat(oneOrder).isEqualTo(otherOrder)
        Truth.assertThat(oneOrder.lastHits(Side.RADIANT)).isEqualTo(1)
    }

    @Test
    fun `a bot is the same for a seed, a pro one lands more hits in the window than an easy one`() {
        val easy = planBot(sim, Side.DIRE, lina, BotLevel.EASY, seed = 5)
        Truth.assertThat(planBot(sim, Side.DIRE, lina, BotLevel.EASY, seed = 5)).isEqualTo(easy)

        val pro = planBot(sim, Side.DIRE, lina, BotLevel.PRO, seed = 5)
        fun hits(plan: List<n7.ad2.games.internal.games.mid.PlannedAttack>) =
            plan.count { attack -> sim.outcome(sim.creep(attack.creepId), lina, attack.landAt) == Outcome.Kill }
        Truth.assertThat(hits(pro)).isGreaterThan(hits(easy))
    }

    @Test
    fun `a bot never swings again before its attack time passed`() {
        val plan = planBot(sim, Side.DIRE, lina, BotLevel.PRO, seed = 8)
        plan.zipWithNext().forEach { (previous, next) ->
            Truth.assertThat(next.startAt - previous.startAt).isAtLeast(lina.attackRateMs.toLong())
        }
    }

    @Test
    fun `network messages survive the round trip, garbage is ignored`() {
        Truth.assertThat(MidCodec.decode(MidCodec.ready("shadow_fiend"))).isEqualTo(MidCodec.Message.Ready("shadow_fiend"))
        Truth.assertThat(MidCodec.decode(MidCodec.go())).isEqualTo(MidCodec.Message.Go)
        Truth.assertThat(MidCodec.decode(MidCodec.claim(creepId = 12, landAt = 41_250))).isEqualTo(MidCodec.Message.Claim(12, 41_250))
        Truth.assertThat(MidCodec.decode(MidCodec.swing(creepId = 3, startAt = 900))).isEqualTo(MidCodec.Message.Swing(3, 900))
        Truth.assertThat(MidCodec.decode("c;x;1")).isNull()
        Truth.assertThat(MidCodec.decode("k;1;2")).isNull()
    }
}
