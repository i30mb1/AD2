package n7.ad2.hero.page.internal.responses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.responses.ResponseRingtoneSetter
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse

/** What can be done with one response: listen, keep it on the phone, or make it the call / notification / alarm sound. */
@Composable
internal fun ResponseActionsSheet(
    item: VOResponse.Body,
    category: String,
    isPlaying: Boolean,
    progress: Float,
    isDownloading: Boolean,
    settingTarget: ResponseRingtoneSetter.Target?,
    onPlayClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onSetAsClick: (target: ResponseRingtoneSetter.Target) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 36.dp, height = 4.dp)
                .clip(CircleShape)
                .background(AppTheme.color.textSecondaryColor.copy(alpha = 0.3f)),
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            PlayButton(isPlaying = isPlaying, onClick = onPlayClick, size = 48)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(text = item.title, style = AppTheme.style.H4, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
                Text(text = "${item.heroName} · $category", style = AppTheme.style.body, color = AppTheme.color.textSecondaryColor)
                LinearProgressIndicator(
                    progress = if (isPlaying) progress else 0f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(CircleShape),
                    color = AppTheme.color.primary,
                    backgroundColor = AppTheme.color.background,
                )
            }
        }

        DownloadRow(isSaved = item.isSavedInMemory, isDownloading = isDownloading, onClick = onDownloadClick)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.response_set_as).uppercase(),
                style = AppTheme.style.body,
                fontWeight = FontWeight.Bold,
                color = AppTheme.color.textSecondaryColor,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TargetTile(Icons.Default.Phone, R.string.response_target_call, settingTarget == ResponseRingtoneSetter.Target.CALL, Modifier.weight(1f)) {
                    onSetAsClick(ResponseRingtoneSetter.Target.CALL)
                }
                TargetTile(Icons.Default.Notifications, R.string.response_target_notification, settingTarget == ResponseRingtoneSetter.Target.NOTIFICATION, Modifier.weight(1f)) {
                    onSetAsClick(ResponseRingtoneSetter.Target.NOTIFICATION)
                }
                TargetTile(Icons.Default.Alarm, R.string.response_target_alarm, settingTarget == ResponseRingtoneSetter.Target.ALARM, Modifier.weight(1f)) {
                    onSetAsClick(ResponseRingtoneSetter.Target.ALARM)
                }
            }
            Text(text = stringResource(R.string.response_write_settings_hint), style = AppTheme.style.body, color = AppTheme.color.textSecondaryColor)
        }
    }
}

@Composable
private fun DownloadRow(isSaved: Boolean, isDownloading: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(AppTheme.shape.medium)
            .background(AppTheme.color.background)
            .clickable(enabled = !isSaved && !isDownloading, role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Download,
            contentDescription = null,
            tint = if (isSaved) Color(0xFF81C784) else AppTheme.color.textColor,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp),
        ) {
            val title = when {
                isSaved -> R.string.response_downloaded
                isDownloading -> R.string.response_downloading
                else -> R.string.response_download
            }
            Text(text = stringResource(title), style = AppTheme.style.H5, fontWeight = FontWeight.Medium, color = AppTheme.color.textColor)
            Text(text = stringResource(R.string.response_download_hint), style = AppTheme.style.body, color = AppTheme.color.textSecondaryColor)
        }
        when {
            isSaved -> Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF81C784))
            isDownloading -> CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = AppTheme.color.primary)
        }
    }
}

@Composable
private fun TargetTile(icon: ImageVector, label: Int, isInProgress: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .height(76.dp)
            .clip(AppTheme.shape.medium)
            .background(if (isInProgress) AppTheme.color.primary.copy(alpha = 0.16f) else AppTheme.color.background)
            .border(1.dp, if (isInProgress) AppTheme.color.primary else Color.Transparent, AppTheme.shape.medium)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (isInProgress) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = AppTheme.color.primary)
        } else {
            Icon(imageVector = icon, contentDescription = null, tint = AppTheme.color.textSecondaryColor)
        }
        Box(Modifier.height(6.dp).width(1.dp))
        Text(text = stringResource(label), style = AppTheme.style.body, fontWeight = FontWeight.Medium, color = AppTheme.color.textColor)
    }
}
