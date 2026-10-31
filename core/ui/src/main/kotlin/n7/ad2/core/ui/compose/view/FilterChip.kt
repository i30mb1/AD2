package n7.ad2.core.ui.compose.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme

private val borderColor = Color(0xFF3A3D43)

/**
 * Pill for the filters above the lists (news source, stream language, match status), the selected one is filled with the primary color
 */
@Composable
fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, hasDot: Boolean = false) {
    Row(
        modifier = modifier
            .height(32.dp)
            .clip(CircleShape)
            .background(if (isSelected) AppTheme.color.primary else Color.Transparent)
            .border(1.dp, if (isSelected) AppTheme.color.primary else borderColor, CircleShape)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (hasDot) {
            Box(
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else AppTheme.color.primary),
            )
        }
        Text(
            text = text,
            style = AppTheme.style.body,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else AppTheme.color.textSecondaryColor,
        )
    }
}

@Suppress("UnusedPrivateMember")
@Preview
@Composable
private fun FilterChipPreview() {
    AppTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip("All", isSelected = true, onClick = {})
            FilterChip("Live", isSelected = false, onClick = {}, hasDot = true)
        }
    }
}
