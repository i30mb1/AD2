package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.ui.adapter.HeaderViewHolder

@Composable
internal fun HeaderItem(data: HeaderViewHolder.Data) {
    Text(
        text = data.title,
        style = AppTheme.style.H4,
        color = AppTheme.color.primary,
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.color.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}
