package n7.ad2.core.ui.compose.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.compose.Bold

/**
 * Заголовок группы в сетке, как item_header: плашка акцентного цвета с белым текстом капсом
 */
@Composable
fun GridHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .background(AppTheme.color.primary, AppTheme.shape.small)
            .padding(4.dp),
        text = title.uppercase(),
        style = AppTheme.style.body.Bold,
        color = Color.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Suppress("UnusedPrivateMember")
@Preview
@Composable
private fun GridHeaderPreview() {
    AppTheme {
        GridHeader("strength")
    }
}
