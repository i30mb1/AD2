package n7.ad2.games.internal.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme

@Composable
internal fun Tabs(items: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(AppTheme.color.surface)
            .padding(3.dp),
    ) {
        items.forEachIndexed { index, title ->
            Text(
                text = title,
                color = if (index == selected) Color.White else AppTheme.color.textSecondaryColor,
                style = AppTheme.style.body,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (index == selected) MeColor else Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}
