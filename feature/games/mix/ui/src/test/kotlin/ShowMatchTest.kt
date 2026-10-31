import com.google.common.truth.Truth
import n7.ad2.games.internal.games.show.RoundAnswer
import n7.ad2.games.internal.games.show.ShowCodec
import n7.ad2.games.internal.games.show.ShowMatch
import n7.ad2.games.internal.games.show.ShowPlan
import n7.ad2.games.internal.games.show.ShowPlayer
import org.junit.Test

internal class ShowMatchTest {

    private val me = ShowPlayer("me", "Me")
    private val pixel = ShowPlayer("p", "Pixel 8")
    private val galaxy = ShowPlayer("g", "Galaxy")
    private val rounds = ShowPlan.create(seed = 3)

    @Test
    fun `alone a round is complete as soon as I answer`() {
        val match = ShowMatch(listOf(me), rounds).answer("me", round = 0, RoundAnswer(correct = true, points = 180))

        Truth.assertThat(match.roundComplete).isTrue()
        Truth.assertThat(match.score("me")).isEqualTo(180)
    }

    @Test
    fun `together a round waits for everyone, answers to the next round wait for it`() {
        var match = ShowMatch(listOf(me, pixel), rounds)
            .answer("p", round = 0, RoundAnswer(correct = true, points = 150))
            .answer("p", round = 1, RoundAnswer(correct = false, points = 0))
        Truth.assertThat(match.roundComplete).isFalse()

        match = match.answer("me", round = 0, RoundAnswer(correct = false, points = -100))
        Truth.assertThat(match.roundComplete).isTrue()

        match = match.nextRound()
        Truth.assertThat(match.round).isEqualTo(1)
        Truth.assertThat(match.answerOf("p")).isEqualTo(RoundAnswer(correct = false, points = 0))
        Truth.assertThat(match.roundComplete).isFalse()
        Truth.assertThat(match.score("p")).isEqualTo(150)
        Truth.assertThat(match.score("me")).isEqualTo(-100)
    }

    @Test
    fun `the first answer of a player in a round counts, a repeat is ignored`() {
        val match = ShowMatch(listOf(me), rounds)
            .answer("me", round = 0, RoundAnswer(correct = true, points = 200))
            .answer("me", round = 0, RoundAnswer(correct = false, points = 0))

        Truth.assertThat(match.score("me")).isEqualTo(200)
    }

    @Test
    fun `the show is over after the final, the standings go by score`() {
        var match = ShowMatch(listOf(me, pixel), rounds)
        rounds.forEach { round ->
            match = match
                .answer("me", round.index, RoundAnswer(correct = true, points = 100))
                .answer("p", round.index, RoundAnswer(correct = true, points = 150))
                .nextRound()
        }

        Truth.assertThat(match.isOver).isTrue()
        Truth.assertThat(match.standings()).containsExactly(pixel, me).inOrder()
        Truth.assertThat(match.teamScore()).isEqualTo(6 * 250)
    }

    @Test
    fun `when the only other player leaves the show is over`() {
        val match = ShowMatch(listOf(me, pixel), rounds).answer("me", round = 0, RoundAnswer(correct = true, points = 100)).leave("p")

        Truth.assertThat(match.isOver).isTrue()
        Truth.assertThat(match.peerLeft).isTrue()
    }

    @Test
    fun `with three players one leaving does not end the show and does not hold the round`() {
        val match = ShowMatch(listOf(me, pixel, galaxy), rounds)
            .answer("me", round = 0, RoundAnswer(correct = true, points = 100))
            .answer("g", round = 0, RoundAnswer(correct = true, points = 120))
            .leave("p")

        Truth.assertThat(match.isOver).isFalse()
        Truth.assertThat(match.roundComplete).isTrue()
    }

    @Test
    fun `answers go over the network and back`() {
        val payload = ShowCodec.encode(round = 4, RoundAnswer(correct = true, points = -100))

        Truth.assertThat(ShowCodec.decode(payload)).isEqualTo(4 to RoundAnswer(correct = true, points = -100))
        Truth.assertThat(ShowCodec.decode("garbage")).isNull()
        Truth.assertThat(ShowCodec.decode("a;x;true;1")).isNull()
    }
}
