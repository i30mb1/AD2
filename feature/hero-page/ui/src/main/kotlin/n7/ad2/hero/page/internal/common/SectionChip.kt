package n7.ad2.hero.page.internal.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme

/**
 * The tab pager sits outside the tab fragment and takes every sideways drag for a tab switch. A touch that starts
 * on a horizontally scrolling row keeps the drag inside the fragment, so the row scrolls and the tab stays.
 */
@Composable
internal fun Modifier.keepsSidewaysDrag(): Modifier {
    val view = LocalView.current
    return pointerInput(view) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            view.parent?.requestDisallowInterceptTouchEvent(true)
        }
    }
}

@Composable
internal fun SectionChip(text: String, count: Int?, isSelected: Boolean, onClick: () -> Unit) {
    val textColor = if (isSelected) Color.White else AppTheme.color.textSecondaryColor
    Row(
        modifier = Modifier
            .height(34.dp)
            .clip(CircleShape)
            .background(if (isSelected) AppTheme.color.primary else AppTheme.color.surface)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = AppTheme.style.body, fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal, color = textColor)
        if (count != null) {
            Text(text = " $count", style = AppTheme.style.body, color = textColor.copy(alpha = 0.7f))
        }
    }
}
