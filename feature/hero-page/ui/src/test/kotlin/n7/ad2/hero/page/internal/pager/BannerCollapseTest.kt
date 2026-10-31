package n7.ad2.hero.page.internal.pager

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import org.junit.Assert.assertEquals
import org.junit.Test

class BannerCollapseTest {

    private val collapse = BannerCollapse(rangePx = 100f)
    private val user = NestedScrollSource.UserInput

    @Test
    fun `scrolling the content up folds the banner before the list moves`() {
        assertEquals(Offset(0f, -30f), collapse.onPreScroll(Offset(0f, -30f), user))
        assertEquals(0.3f, collapse.fraction, 0.001f)
        // only the rest of the range is taken, the list scrolls the remainder
        assertEquals(Offset(0f, -70f), collapse.onPreScroll(Offset(0f, -100f), user))
        assertEquals(1f, collapse.fraction, 0.001f)
    }

    @Test
    fun `the banner opens only after the list is back at its top`() {
        collapse.onPreScroll(Offset(0f, -100f), user)
        // pulling down first scrolls the list, so nothing is taken before it
        assertEquals(Offset.Zero, collapse.onPreScroll(Offset(0f, 40f), user))
        // what the list could not use opens the banner
        assertEquals(Offset(0f, 40f), collapse.onPostScroll(Offset.Zero, Offset(0f, 40f), user))
        assertEquals(0.6f, collapse.fraction, 0.001f)
    }

    @Test
    fun `sideways swipes between tabs are left to the pager`() {
        assertEquals(Offset.Zero, collapse.onPreScroll(Offset(-50f, 0f), user))
        assertEquals(0f, collapse.fraction, 0.001f)
    }
}
