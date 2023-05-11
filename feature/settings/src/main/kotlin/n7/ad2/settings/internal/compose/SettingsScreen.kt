package n7.ad2.settings.internal.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.feature.settings.R
import n7.ad2.ui.compose.AppTheme

@Composable
internal fun SettingsScreen(
    onAppReviewButtonClicked: () -> Unit,
    onTellFriendsButtonClicked: () -> Unit,
) {
    Column {
        SimpleItem(stringResource(R.string.setting_review_app), onAppReviewButtonClicked)
        SimpleItem(stringResource(R.string.setting_tell_friend_about_this_app), onTellFriendsButtonClicked)
    }
}

@Composable
internal fun SimpleItem(
    name: String,
    onSimpleItemClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.color.surface)
            .clickable { onSimpleItemClicked() }
            .padding(12.dp, 8.dp),
    ) {
        Text(
            text = name,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f, false),
            color = AppTheme.color.textColor
        )
        Icon(
            Icons.Default.Star,
            null,
        )
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen({}, {})
}