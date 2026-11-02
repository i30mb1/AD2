import com.google.common.truth.Truth
import n7.ad2.games.internal.games.show.RoundAnswer
import n7.ad2.games.internal.games.show.ShowKind
import n7.ad2.games.internal.games.show.ShowMatch
import n7.ad2.games.internal.games.show.ShowPlan
import n7.ad2.games.internal.games.show.ShowPlayer
import n7.ad2.games.internal.games.show.ShowQuestion
import n7.ad2.games.internal.games.show.ShowViewModel
import n7.ad2.games.internal.games.show.compose.stage
import n7.ad2.games.room.GameMode
import org.junit.Test

/** The middle of the show fades from stage to stage: a new stage for a new round or part of it, not for every answer */
internal class ShowStageTest {

    private val match = ShowMatch(listOf(ShowPlayer("me", "Me")), ShowPlan.create(1))
    private val question = ShowQuestion.Pick(ShowKind.HERO, spellImage = null, spellLevel = 0, voice = null, options = emptyList())
    private val start = ShowViewModel.State.start(GameMode.SOLO, "me", match)

    @Test
    fun `the countdown, the intro and the question of a round are different stages`() {
        val stages = listOf(start.copy(countdown = 3), start, start.copy(question = question), start.copy(loadFailed = true)).map { it.stage() }

        Truth.assertThat(stages.toSet()).hasSize(4)
    }

    @Test
    fun `an answer and its reveal stay on the stage of the question`() {
        val asked = start.copy(phase = ShowViewModel.State.Phase.QUESTION, question = question)
        val answered = asked.copy(phase = ShowViewModel.State.Phase.REVEAL, picked = 1, myAnswer = RoundAnswer(correct = true, points = 150))

        Truth.assertThat(answered.stage()).isEqualTo(asked.stage())
    }

    @Test
    fun `the next round is a new stage`() {
        val asked = start.copy(question = question)

        Truth.assertThat(asked.copy(match = match.nextRound()).stage()).isNotEqualTo(asked.stage())
    }
}
