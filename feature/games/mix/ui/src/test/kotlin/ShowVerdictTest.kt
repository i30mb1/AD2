import com.google.common.truth.Truth
import n7.ad2.games.internal.games.show.RoundAnswer
import n7.ad2.games.internal.games.show.ShowMatch
import n7.ad2.games.internal.games.show.ShowPlan
import n7.ad2.games.internal.games.show.ShowPlayer
import n7.ad2.games.internal.games.show.ShowViewModel
import n7.ad2.games.internal.games.show.compose.Verdict
import n7.ad2.games.internal.games.show.compose.verdict
import n7.ad2.games.room.GameMode
import org.junit.Test

/** After an answer the screen says it big: right, a miss, or the fuse burned before any answer */
internal class ShowVerdictTest {

    private val start = ShowViewModel.State.start(GameMode.SOLO, "me", ShowMatch(listOf(ShowPlayer("me", "Me")), ShowPlan.create(1)))
    private val reveal = start.copy(phase = ShowViewModel.State.Phase.REVEAL)

    @Test
    fun `nothing to say before the answer`() {
        Truth.assertThat(start.verdict()).isNull()
    }

    @Test
    fun `a right answer is right`() {
        Truth.assertThat(reveal.copy(picked = 1, myAnswer = RoundAnswer(correct = true, points = 170)).verdict()).isEqualTo(Verdict.RIGHT)
    }

    @Test
    fun `a wrong pick is a miss`() {
        Truth.assertThat(reveal.copy(picked = 2, myAnswer = RoundAnswer(correct = false, points = 0)).verdict()).isEqualTo(Verdict.MISS)
    }

    @Test
    fun `a burned fuse is the time out, not a miss`() {
        Truth.assertThat(reveal.copy(myAnswer = RoundAnswer(correct = false, points = 0), timedOut = true).verdict()).isEqualTo(Verdict.LATE)
    }
}
