import com.google.common.truth.Truth
import n7.ad2.games.internal.games.killCreep.CreepSim
import n7.ad2.games.internal.games.killCreep.CreepType
import org.junit.Test

internal class CreepSimTest {

    @Test
    fun `same seed gives the same field`() {
        val a = CreepSim(seed = 42)
        val b = CreepSim(seed = 42)

        Truth.assertThat(a.creeps).isEqualTo(b.creeps)
        for (t in listOf(0L, 5_000L, 30_000L, 59_000L)) {
            Truth.assertThat(a.visibleAt(t)).isEqualTo(b.visibleAt(t))
        }
    }

    @Test
    fun `different seeds give different fields`() {
        Truth.assertThat(CreepSim(seed = 1).creeps).isNotEqualTo(CreepSim(seed = 2).creeps)
    }

    @Test
    fun `creeps stay inside the field`() {
        val sim = CreepSim(seed = 7)
        for (t in 0L..CreepSim.DURATION_MS step 250) {
            sim.visibleAt(t).forEach { creep ->
                Truth.assertThat(creep.x).isIn(com.google.common.collect.Range.closed(0f, 1f))
                Truth.assertThat(creep.y).isIn(com.google.common.collect.Range.closed(0f, 1f))
            }
        }
    }

    @Test
    fun `hp drops and a creep becomes killable before allies finish it`() {
        val sim = CreepSim(seed = 3)
        sim.creeps.forEach { creep ->
            Truth.assertThat(creep.killableAt).isGreaterThan(creep.spawnAt)
            Truth.assertThat(creep.deathAt).isGreaterThan(creep.killableAt)
            Truth.assertThat(sim.hpAt(creep, creep.spawnAt)).isEqualTo(creep.type.hp)
            Truth.assertThat(sim.hpAt(creep, creep.killableAt)).isAtMost(creep.type.lastHitDamage)
            Truth.assertThat(sim.hpAt(creep, creep.killableAt - 1)).isGreaterThan(creep.type.lastHitDamage)
        }
    }

    @Test
    fun `a creep is visible only between spawn and death`() {
        val sim = CreepSim(seed = 5)
        val creep = sim.creeps.first()

        Truth.assertThat(sim.visibleAt(creep.spawnAt).map { it.id }).contains(creep.id)
        Truth.assertThat(sim.visibleAt(creep.deathAt).map { it.id }).doesNotContain(creep.id)
    }

    @Test
    fun `the field gets busier and has one mega creep`() {
        val sim = CreepSim(seed = 11)
        val early = (0L until 15_000L step 500).maxOf { sim.visibleAt(it).size }
        val late = (45_000L until 60_000L step 500).maxOf { sim.visibleAt(it).size }

        Truth.assertThat(late).isGreaterThan(early)
        Truth.assertThat(sim.creeps.count { it.type == CreepType.MEGA }).isEqualTo(1)
        Truth.assertThat(sim.creeps.map { it.type }.toSet().size).isAtLeast(3)
    }
}
