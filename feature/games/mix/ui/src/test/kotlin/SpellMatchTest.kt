import com.google.common.truth.Truth
import n7.ad2.games.internal.games.skillmp.Answer
import n7.ad2.games.internal.games.skillmp.SpellMatch
import n7.ad2.games.room.GameMode
import org.junit.Test

internal class SpellMatchTest {

    private val right = Answer(correct = true, timeMs = 1000)
    private val slowRight = Answer(correct = true, timeMs = 2000)
    private val wrong = Answer(correct = false, timeMs = 500)

    private fun SpellMatch.play(mine: Answer, peer: Answer? = null): SpellMatch {
        var match = answer(mine = true, mine)
        if (peer != null) match = match.answer(mine = false, peer)
        return if (match.roundComplete && !match.isOver) match.nextRound() else match
    }

    @Test
    fun `solo counts correct answers and ends after three mistakes`() {
        var match = SpellMatch.start(GameMode.SOLO)
        Truth.assertThat(match.lives).isEqualTo(3)

        match = match.play(right).play(right).play(wrong).play(wrong)
        Truth.assertThat(match.myScore).isEqualTo(2)
        Truth.assertThat(match.lives).isEqualTo(1)
        Truth.assertThat(match.isOver).isFalse()

        match = match.play(wrong)
        Truth.assertThat(match.isOver).isTrue()
    }

    @Test
    fun `solo round completes right after my answer`() {
        Truth.assertThat(SpellMatch.start(GameMode.SOLO).answer(mine = true, right).roundComplete).isTrue()
    }

    @Test
    fun `versus round waits for both answers`() {
        val match = SpellMatch.start(GameMode.VERSUS).answer(mine = true, right)

        Truth.assertThat(match.roundComplete).isFalse()
        Truth.assertThat(match.answer(mine = false, wrong).roundComplete).isTrue()
    }

    @Test
    fun `versus faster correct answer gets two points, slower one`() {
        val match = SpellMatch.start(GameMode.VERSUS).play(mine = right, peer = slowRight)

        Truth.assertThat(match.myScore).isEqualTo(2)
        Truth.assertThat(match.peerScore).isEqualTo(1)
        Truth.assertThat(match.round).isEqualTo(1)
    }

    @Test
    fun `versus correct against wrong gets one point, both wrong nothing`() {
        var match = SpellMatch.start(GameMode.VERSUS).play(mine = slowRight, peer = wrong)
        Truth.assertThat(match.myScore).isEqualTo(1)
        Truth.assertThat(match.peerScore).isEqualTo(0)

        match = match.play(mine = wrong, peer = wrong)
        Truth.assertThat(match.myScore).isEqualTo(1)
        Truth.assertThat(match.peerScore).isEqualTo(0)
    }

    @Test
    fun `peer answer may arrive before mine`() {
        val match = SpellMatch.start(GameMode.VERSUS).answer(mine = false, right).answer(mine = true, slowRight)

        Truth.assertThat(match.roundComplete).isTrue()
        Truth.assertThat(match.peerScore).isEqualTo(2)
        Truth.assertThat(match.myScore).isEqualTo(1)
    }

    @Test
    fun `versus ends after ten rounds`() {
        var match = SpellMatch.start(GameMode.VERSUS)
        repeat(9) { match = match.play(mine = right, peer = wrong) }
        Truth.assertThat(match.isOver).isFalse()

        match = match.play(mine = right, peer = wrong)
        Truth.assertThat(match.isOver).isTrue()
        Truth.assertThat(match.myScore).isEqualTo(10)
    }

    @Test
    fun `coop shares lives and score`() {
        var match = SpellMatch.start(GameMode.COOP)

        match = match.play(mine = right, peer = right)
        Truth.assertThat(match.myScore).isEqualTo(2)
        match = match.play(mine = wrong, peer = right)
        Truth.assertThat(match.lives).isEqualTo(2)
        Truth.assertThat(match.myScore).isEqualTo(3)
        match = match.play(mine = wrong, peer = wrong)
        Truth.assertThat(match.lives).isEqualTo(0)
        Truth.assertThat(match.isOver).isTrue()
    }

    @Test
    fun `peer leaving ends the game`() {
        val match = SpellMatch.start(GameMode.VERSUS).peerLeft()

        Truth.assertThat(match.isOver).isTrue()
        Truth.assertThat(match.peerLeft).isTrue()
    }
}
