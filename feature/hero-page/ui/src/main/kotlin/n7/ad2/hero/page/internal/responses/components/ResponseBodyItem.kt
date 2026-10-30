package n7.ad2.hero.page.internal.responses.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse

/** Play button, the line itself with a progress bar while it plays, and the "more" button for download / ringtone. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ResponseBodyItem(
    item: VOResponse.Body,
    isPlaying: Boolean,
    progress: Float,
    onPlayClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .combinedClickable(onClick = onPlayClick, onLongClick = onMoreClick)
            .padding(start = 12.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayButton(isPlaying = isPlaying, onClick = onPlayClick)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, top = 8.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = item.title,
                style = AppTheme.style.H5,
                color = if (isPlaying) AppTheme.color.textColor else AppTheme.color.textColor.copy(alpha = 0.9f),
            )
            if (isPlaying) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(CircleShape),
                    color = AppTheme.color.primary,
                    backgroundColor = AppTheme.color.surface,
                )
            }
        }
        if (item.icons.isNotEmpty()) {
            Row(
                modifier = Modifier.padding(start = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                item.icons.forEach { ResponseImage(it) }
            }
        }
        if (item.isSavedInMemory) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = stringResource(R.string.response_downloaded),
                tint = Color(0xFF81C784),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(16.dp),
            )
        }
        IconButton(onClick = onMoreClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.response_actions),
                tint = AppTheme.color.textSecondaryColor,
            )
        }
    }
}

@Composable
internal fun PlayButton(isPlaying: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, size: Int = 36) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(if (isPlaying) AppTheme.color.primary else Color.Transparent)
            .border(1.5.dp, AppTheme.color.primary, CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = stringResource(if (isPlaying) R.string.response_pause else R.string.response_play),
            tint = if (isPlaying) Color.White else AppTheme.color.primary,
            modifier = Modifier.size((size / 2).dp),
        )
    }
}
