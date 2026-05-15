package n7.ad2.hero.page.internal.info.components

import android.text.Spanned
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.hero.page.internal.info.domain.vo.VOBodyLine
import n7.ad2.hero.page.internal.info.domain.vo.VOBodySimple
import n7.ad2.hero.page.internal.info.domain.vo.VOBodyWithImage
import n7.ad2.ui.adapter.BodyViewHolder

@Composable
internal fun BodyItem(data: BodyViewHolder.Data) {
    val textColor = AppTheme.color.textColor.toArgb()
    AndroidView(
        factory = { ctx -> TextView(ctx).apply { setTextColor(textColor) } },
        update = { tv -> tv.text = data.text },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    )
}

@Composable
internal fun BodySimpleItem(item: VOBodySimple) {
    Text(
        text = item.body,
        style = AppTheme.style.body,
        color = AppTheme.color.textColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    )
}

@Composable
internal fun BodyLineItem(item: VOBodyLine) {
    SpannedTextView(item.title, Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp))
}

@Composable
internal fun BodyWithImageItem(item: VOBodyWithImage) {
    val textColor = AppTheme.color.textColor.toArgb()
    AndroidView(
        factory = { ctx ->
            LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                addView(ImageView(ctx).apply { setImageResource(item.drawable) })
                addView(TextView(ctx).apply { setTextColor(textColor) })
            }
        },
        update = { row ->
            (row.getChildAt(1) as TextView).text = item.body
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    )
}

@Composable
private fun SpannedTextView(content: Spanned, modifier: Modifier = Modifier) {
    val textColor = AppTheme.color.textColor.toArgb()
    AndroidView(
        factory = { ctx -> TextView(ctx).apply { setTextColor(textColor) } },
        update = { tv -> tv.text = content },
        modifier = modifier,
    )
}
