import com.google.common.truth.Truth
import n7.ad2.games.internal.games.mid.CreepType
import n7.ad2.games.internal.games.mid.Side
import n7.ad2.games.internal.paper.PaperCreeps
import n7.ad2.games.internal.paper.PaperLane
import n7.ad2.games.internal.paper.PaperLook
import n7.ad2.games.internal.paper.PaperPart
import org.junit.Test

internal class PaperArtTest {

    @Test
    fun `every creep type has a paper figure on both sides`() {
        for (type in CreepType.entries) {
            for (side in Side.entries) {
                val figure = PaperCreeps.figure(type, side)
                Truth.assertWithMessage("$type $side body").that(figure.body).isNotEmpty()
                Truth.assertWithMessage("$type $side details").that(figure.details).isNotEmpty()
            }
        }
    }

    @Test
    fun `creep figures fit their 100x100 frame`() {
        for (type in CreepType.entries) {
            for (side in Side.entries) {
                val figure = PaperCreeps.figure(type, side)
                (figure.body + figure.details).forEach { part -> assertInside(part, 100f, 100f, "$type $side") }
            }
        }
    }

    @Test
    fun `Radiant and Dire creeps are cut from different paper`() {
        Truth.assertThat(PaperCreeps.figure(CreepType.MELEE, Side.RADIANT)).isNotEqualTo(PaperCreeps.figure(CreepType.MELEE, Side.DIRE))
    }

    @Test
    fun `lane parts stay inside its frame`() {
        PaperLane.parts.forEach { part -> assertInside(part, PaperLane.WIDTH, PaperLane.HEIGHT, "lane", margin = 60f) }
    }

    @Test
    fun `a creep in reach of a hit gets a gold edge and lifts off the field`() {
        val normal = PaperLook.of(killable = false)
        val killable = PaperLook.of(killable = true)

        Truth.assertThat(killable.edge).isNotEqualTo(normal.edge)
        Truth.assertThat(killable.lift).isGreaterThan(normal.lift)
        Truth.assertThat(killable.tilt).isNotEqualTo(0f)
        Truth.assertThat(normal.tilt).isEqualTo(0f)
    }

    private fun assertInside(part: PaperPart, width: Float, height: Float, label: String, margin: Float = 0f) {
        val (left, top, right, bottom) = part.bounds()
        Truth.assertWithMessage("$label $part").that(left).isAtLeast(-margin)
        Truth.assertWithMessage("$label $part").that(top).isAtLeast(-margin)
        Truth.assertWithMessage("$label $part").that(right).isAtMost(width + margin)
        Truth.assertWithMessage("$label $part").that(bottom).isAtMost(height + margin)
    }
}
