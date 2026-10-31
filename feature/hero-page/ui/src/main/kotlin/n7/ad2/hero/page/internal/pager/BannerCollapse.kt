package n7.ad2.hero.page.internal.pager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

/**
 * Folds the hero banner into the toolbar while a tab's list scrolls up and opens it again once the list is
 * back at its top. [fraction] is 0 for an open banner and 1 for a folded one.
 */
internal class BannerCollapse(private val rangePx: Float) : NestedScrollConnection {

    private var collapsedPx by mutableFloatStateOf(0f)

    val fraction: Float get() = if (rangePx > 0f) collapsedPx / rangePx else 0f

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset =
        if (available.y < 0f) Offset(0f, consume(available.y)) else Offset.Zero

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset =
        if (available.y > 0f) Offset(0f, consume(available.y)) else Offset.Zero

    /** [dy] < 0 folds, > 0 opens; returns the part that went into the banner. */
    private fun consume(dy: Float): Float {
        val new = (collapsedPx - dy).coerceIn(0f, rangePx)
        val used = collapsedPx - new
        collapsedPx = new
        return used
    }
}

/**
 * The tabs are fragments with their own ComposeView, the View nested scroll interop between them and the pager loses
 * the events. Compose inside AndroidView inherits the parent composition, so the pager hands its connection down.
 */
internal val LocalBannerCollapse = staticCompositionLocalOf<NestedScrollConnection?> { null }

/** Put on the scrolling list of a tab so the hero banner folds with it. */
@Composable
internal fun Modifier.foldsHeroBanner(): Modifier = LocalBannerCollapse.current?.let { nestedScroll(it) } ?: this
