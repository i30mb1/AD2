import com.google.common.truth.Truth
import n7.ad2.games.internal.MatchClock
import org.junit.Test

internal class MatchClockTest {

    private val clock = MatchClock(startedAt = 10_000, countdownMs = 3_000)

    @Test
    fun `WHEN countdown is running THEN match time is negative and seconds count down`() {
        Truth.assertThat(clock.elapsed(10_000)).isEqualTo(-3_000)
        Truth.assertThat(clock.countdown(10_000)).isEqualTo(3)
        Truth.assertThat(clock.countdown(11_001)).isEqualTo(2)
        Truth.assertThat(clock.countdown(12_999)).isEqualTo(1)
    }

    @Test
    fun `WHEN countdown is over THEN match time starts from zero`() {
        Truth.assertThat(clock.elapsed(13_000)).isEqualTo(0)
        Truth.assertThat(clock.countdown(13_000)).isEqualTo(0)
        Truth.assertThat(clock.elapsed(14_500)).isEqualTo(1_500)
    }
}
