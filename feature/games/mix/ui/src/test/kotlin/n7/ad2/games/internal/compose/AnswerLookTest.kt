package n7.ad2.games.internal.compose

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AnswerLookTest {

    @Test
    fun `before the reveal only the picked answer is marked`() {
        assertThat(answerLook(isRight = true, isPicked = false, reveal = false)).isEqualTo(AnswerLook.IDLE)
        assertThat(answerLook(isRight = false, isPicked = true, reveal = false)).isEqualTo(AnswerLook.PICKED)
    }

    @Test
    fun `after the reveal the right answer is green and a wrong pick is red`() {
        assertThat(answerLook(isRight = true, isPicked = false, reveal = true)).isEqualTo(AnswerLook.RIGHT)
        assertThat(answerLook(isRight = true, isPicked = true, reveal = true)).isEqualTo(AnswerLook.RIGHT)
        assertThat(answerLook(isRight = false, isPicked = true, reveal = true)).isEqualTo(AnswerLook.WRONG)
        assertThat(answerLook(isRight = false, isPicked = false, reveal = true)).isEqualTo(AnswerLook.IDLE)
    }

    @Test
    fun `avatar shows the first letter of the name`() {
        assertThat(avatarLetter("pixel 8")).isEqualTo("P")
        assertThat(avatarLetter("  ")).isEqualTo("?")
    }
}
