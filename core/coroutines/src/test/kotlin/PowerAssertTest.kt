import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class PowerAssertTest {

    @Test
    fun `failed assert shows values of every part of the expression`() {
        val hero = "Pudge"

        val error = assertThrows(AssertionError::class.java) { assert(hero.length == 3) }

        assertThat(error.message).contains("hero.length == 3")
        assertThat(error.message).contains("Pudge")
    }
}
