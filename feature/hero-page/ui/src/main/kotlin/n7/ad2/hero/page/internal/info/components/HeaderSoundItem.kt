package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo

@Composable
internal fun HeaderSoundItem(item: VOHeroInfo.HeaderSound, onPlayClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = item.title,
            style = AppTheme.style.H4,
            color = AppTheme.color.textColor,
            modifier = Modifier.weight(1f),
        )
        item.hotkey?.let {
            Text(text = it, style = AppTheme.style.body, color = AppTheme.color.textSecondaryColor)
        }
        item.soundUrl?.let { url ->
            IconButton(onClick = { onPlayClick(url) }) {
                Icon(
                    imageVector = if (item.isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = AppTheme.color.primary,
                )
            }
        }
    }
}
