import com.google.common.truth.Truth
import n7.ad2.games.internal.games.skillmp.RecipeItem
import n7.ad2.games.internal.games.skillmp.createBuildQuestion
import org.junit.Test
import kotlin.random.Random

internal class BuildQuestionTest {

    private val basics = (1..12).map { RecipeItem("basic$it", "Basic $it", cost = 100 * it, components = emptyList()) }

    private fun items(vararg crafted: RecipeItem) = basics + crafted

    @Test
    fun `recipe is a correct tile when components are cheaper than the item`() {
        val bracer = RecipeItem("bracer", "Bracer", cost = 505, components = listOf("basic1", "basic2"))

        val question = createBuildQuestion(items(bracer), Random(1))

        Truth.assertThat(question.target).isEqualTo(bracer)
        Truth.assertThat(question.slots).isEqualTo(3)
        Truth.assertThat(question.tiles.filter { it.isCorrect }.map { it.item?.key }).containsExactly("basic1", "basic2", null)
    }

    @Test
    fun `recipe is a decoy when components cover the whole cost`() {
        val sange = RecipeItem("pair", "Pair", cost = 700, components = listOf("basic3", "basic4"))

        val question = createBuildQuestion(items(sange), Random(2))

        Truth.assertThat(question.slots).isEqualTo(2)
        Truth.assertThat(question.tiles.single { it.item == null }.isCorrect).isFalse()
    }

    @Test
    fun `always eight tiles, decoys are cheaper than the target and not part of it`() {
        val big = RecipeItem("big", "Big", cost = 1000, components = listOf("basic2", "basic5"))

        val question = createBuildQuestion(items(big), Random(3))

        Truth.assertThat(question.tiles).hasSize(8)
        question.tiles.filter { !it.isCorrect && it.item != null }.forEach { tile ->
            Truth.assertThat(tile.item!!.cost).isLessThan(1000)
            Truth.assertThat(tile.item!!.key).isNoneOf("basic2", "basic5", "big")
        }
        Truth.assertThat(question.tiles.count { it.item == null }).isEqualTo(1)
    }

    @Test
    fun `duplicate component gives two correct tiles`() {
        val double = RecipeItem("double", "Double", cost = 500, components = listOf("basic2", "basic2"))

        val question = createBuildQuestion(items(double), Random(4))

        Truth.assertThat(question.tiles.count { it.isCorrect && it.item?.key == "basic2" }).isEqualTo(2)
    }

    @Test
    fun `same seed gives the same question`() {
        val crafted = (1..5).map { RecipeItem("c$it", "C$it", cost = 2000 + it, components = listOf("basic$it", "basic${it + 1}")) }

        val a = createBuildQuestion(items(*crafted.toTypedArray()), Random(99))
        val b = createBuildQuestion(items(*crafted.toTypedArray()), Random(99))

        Truth.assertThat(a).isEqualTo(b)
    }
}
