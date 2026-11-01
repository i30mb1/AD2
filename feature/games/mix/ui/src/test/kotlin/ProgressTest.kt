import com.google.common.truth.Truth
import n7.ad2.games.internal.progress.GameXp
import n7.ad2.games.internal.progress.Progress
import org.junit.Test

internal class ProgressTest {

    @Test
    fun `a new player is on the first level, each level needs 50 more than the one before`() {
        Truth.assertThat(Progress(0)).isEqualTo(Progress(0))
        Truth.assertThat(Progress(0).level).isEqualTo(1)
        Truth.assertThat(Progress(0).needed).isEqualTo(200)
        Truth.assertThat(Progress(199).level).isEqualTo(1)
        Truth.assertThat(Progress(199).inLevel).isEqualTo(199)
        Truth.assertThat(Progress(200).level).isEqualTo(2)
        Truth.assertThat(Progress(200).inLevel).isEqualTo(0)
        Truth.assertThat(Progress(200).needed).isEqualTo(250)
    }

    @Test
    fun `the seventh level needs 500`() {
        val before = (1..6).sumOf { Progress.needed(it) }

        val progress = Progress(before + 340)

        Truth.assertThat(progress.level).isEqualTo(7)
        Truth.assertThat(progress.inLevel).isEqualTo(340)
        Truth.assertThat(progress.needed).isEqualTo(500)
        Truth.assertThat(progress.fraction).isWithin(0.001f).of(0.68f)
    }

    @Test
    fun `a game adds its points and may take the player to the next level`() {
        val near = Progress(190)

        Truth.assertThat(near + 5).isEqualTo(Progress(195))
        Truth.assertThat((near + 30).level).isEqualTo(2)
        Truth.assertThat(near.levelsUpTo(near + 30)).isEqualTo(1)
        Truth.assertThat(near.levelsUpTo(near + 5)).isEqualTo(0)
    }

    @Test
    fun `a finished game gives 30, a win or a new record 20 more, the challenge of the day 50`() {
        Truth.assertThat(GameXp.game(won = false, record = false)).isEqualTo(30)
        Truth.assertThat(GameXp.game(won = true, record = false)).isEqualTo(50)
        Truth.assertThat(GameXp.game(won = false, record = true)).isEqualTo(50)
        Truth.assertThat(GameXp.game(won = true, record = true)).isEqualTo(50)
        Truth.assertThat(GameXp.DAILY).isEqualTo(50)
    }
}
