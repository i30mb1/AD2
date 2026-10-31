import com.google.common.truth.Truth
import n7.ad2.games.internal.games.mid.CreepType
import n7.ad2.games.internal.games.mid.LaneSim
import n7.ad2.games.internal.games.mid.MidHero
import n7.ad2.games.internal.games.mid.Outcome
import n7.ad2.games.internal.games.mid.Side
import n7.ad2.games.internal.games.mid.landAt
import n7.ad2.games.internal.games.mid.outcome
import org.junit.Test

internal class MidLaneTest {

    private val juggernaut = MidHero("juggernaut", "Juggernaut", "", damage = 55, attackPointMs = 330, attackRateMs = 1_400, projectileSpeed = 0)
    private val sniper = MidHero("sniper", "Sniper", "", damage = 43, attackPointMs = 170, attackRateMs = 1_700, projectileSpeed = 3_000)

    @Test
    fun `the same seed gives the same lane, another seed a different one`() {
        Truth.assertThat(LaneSim(seed = 42).creeps).isEqualTo(LaneSim(seed = 42).creeps)
        Truth.assertThat(LaneSim(seed = 1).creeps).isNotEqualTo(LaneSim(seed = 2).creeps)
    }

    @Test
    fun `every wave comes from both bases and meets in the middle`() {
        val sim = LaneSim(seed = 7)
        for (wave in 0 until LaneSim.WAVES) {
            val radiant = sim.creeps.filter { it.wave == wave && it.side == Side.RADIANT }
            val dire = sim.creeps.filter { it.wave == wave && it.side == Side.DIRE }
            Truth.assertThat(radiant.size).isAtLeast(4)
            Truth.assertThat(dire.map { it.type }).isEqualTo(radiant.map { it.type })
            radiant.forEach { creep ->
                Truth.assertThat(sim.positionAt(creep, creep.spawnAt).t).isEqualTo(0f)
                Truth.assertThat(sim.positionAt(creep, creep.arriveAt)).isEqualTo(creep.slot)
                Truth.assertThat(creep.slot.t).isLessThan(0.5f)
            }
            dire.forEach { creep ->
                Truth.assertThat(sim.positionAt(creep, creep.spawnAt).t).isEqualTo(1f)
                Truth.assertThat(creep.slot.t).isGreaterThan(0.5f)
            }
        }
    }

    @Test
    fun `a creep loses health only from hits and dies at its death moment`() {
        val sim = LaneSim(seed = 3)
        sim.creeps.forEach { creep ->
            var previous = creep.type.hp
            for (time in creep.spawnAt until creep.deathAt step 100) {
                val hp = sim.hpAt(creep, time)
                Truth.assertThat(hp).isAtMost(previous)
                Truth.assertThat(hp).isGreaterThan(0)
                previous = hp
            }
            Truth.assertThat(sim.isAlive(creep, creep.deathAt - 1)).isTrue()
            Truth.assertThat(sim.isAlive(creep, creep.deathAt)).isFalse()
        }
    }

    @Test
    fun `every creep can be taken by the weakest hero before it dies`() {
        val sim = LaneSim(seed = 11)
        sim.creeps.forEach { creep ->
            val killable = sim.killableFrom(creep, sniper.damage)
            Truth.assertWithMessage("${creep.type} ${creep.id}").that(killable).isNotNull()
            Truth.assertThat(creep.deathAt - killable!!).isAtLeast(800L)
        }
    }

    @Test
    fun `siege creeps take less hero damage`() {
        val sim = LaneSim(seed = 5)
        val siege = sim.creeps.first { it.type == CreepType.SIEGE }
        Truth.assertThat(sim.killableFrom(siege, 1_000)).isEqualTo(siege.hits.first { sim.hpAt(siege, it.at) <= 700 }.at)
    }

    @Test
    fun `the lane ends after the last creep is gone`() {
        val sim = LaneSim(seed = 9)
        Truth.assertThat(sim.endsAt).isGreaterThan(sim.creeps.maxOf { it.deathAt })
    }

    @Test
    fun `a melee hit lands after the swing, a ranged one also has to fly`() {
        Truth.assertThat(juggernaut.landAt(startAt = 1_000, distance = 600f)).isEqualTo(1_330)
        Truth.assertThat(sniper.landAt(startAt = 1_000, distance = 600f)).isEqualTo(1_370)
    }

    @Test
    fun `a hit is early above the hero damage, takes the creep in the window and is late after death`() {
        val sim = LaneSim(seed = 13)
        val creep = sim.creeps.first { it.type == CreepType.MELEE }
        val killable = sim.killableFrom(creep, juggernaut.damage)!!

        val early = sim.outcome(creep, juggernaut, landAt = killable - 300)
        Truth.assertThat(early).isInstanceOf(Outcome.Early::class.java)
        Truth.assertThat((early as Outcome.Early).byMs).isEqualTo(300)
        Truth.assertThat(early.hp).isEqualTo(sim.hpAt(creep, killable - 300))

        Truth.assertThat(sim.outcome(creep, juggernaut, landAt = killable)).isEqualTo(Outcome.Kill)
        Truth.assertThat(sim.outcome(creep, juggernaut, landAt = creep.deathAt + 50)).isEqualTo(Outcome.Late(byMs = 50))
    }
}
