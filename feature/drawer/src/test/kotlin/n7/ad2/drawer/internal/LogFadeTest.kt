package n7.ad2.drawer.internal

import com.google.common.truth.Truth
import n7.ad2.drawer.internal.adapter.LogFade
import org.junit.Test

internal class LogFadeTest {

    @Test
    fun `logs inside the fully visible zone are opaque`() {
        Truth.assertThat(LogFade.alpha(top = 300, fadeEnd = 200)).isEqualTo(1f)
        Truth.assertThat(LogFade.alpha(top = 200, fadeEnd = 200)).isEqualTo(1f)
    }

    @Test
    fun `log at the top edge is invisible`() {
        Truth.assertThat(LogFade.alpha(top = 0, fadeEnd = 200)).isEqualTo(0f)
        Truth.assertThat(LogFade.alpha(top = -10, fadeEnd = 200)).isEqualTo(0f)
    }

    @Test
    fun `the higher the log the less it is visible`() {
        Truth.assertThat(LogFade.alpha(top = 100, fadeEnd = 200)).isEqualTo(0.5f)
        Truth.assertThat(LogFade.alpha(top = 50, fadeEnd = 200)).isLessThan(LogFade.alpha(top = 150, fadeEnd = 200))
    }

    @Test
    fun `no fade when all logs fit into the fully visible zone`() {
        Truth.assertThat(LogFade.alpha(top = 0, fadeEnd = 0)).isEqualTo(1f)
        Truth.assertThat(LogFade.alpha(top = 0, fadeEnd = -50)).isEqualTo(1f)
    }
}
