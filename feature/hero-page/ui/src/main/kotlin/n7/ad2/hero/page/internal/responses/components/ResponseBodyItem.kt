package n7.ad2.hero.page.internal.responses.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ResponseBodyItem(
    item: VOResponse.Body,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .background(if (item.isSavedInMemory) AppTheme.color.primary.copy(alpha = 0.1f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = item.title,
            style = AppTheme.style.body,
            color = AppTheme.color.textColor,
            modifier = Modifier.weight(1f),
        )
        if (item.icons.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                items(
                    items = item.icons,
                    key = { it.heroName + it.imageUrl },
                ) { ResponseImage(it) }
            }
        }
    }
}
