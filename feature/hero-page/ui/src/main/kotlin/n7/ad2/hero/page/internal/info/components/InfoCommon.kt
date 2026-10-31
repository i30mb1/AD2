package n7.ad2.hero.page.internal.info.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToLong
import n7.ad2.core.ui.compose.AppTheme

/** Colors the game uses for the same things, so the screen reads like the in-game tooltips. */
internal object InfoColors {
    val strength = Color(0xFFEC3D06)
    val agility = Color(0xFF26E030)
    val intelligence = Color(0xFF00D9EC)
    val universal = Color(0xFFD9B44A)
    val physical = Color(0xFFD9573F)
    val magical = Color(0xFF5B93D1)
    val pure = Color(0xFFD8AF54)
    val talent = Color(0xFF8FD17F)
    val aghanim = Color(0xFF4FA8E8)
    val cooldown = Color(0xFFB8BCC4)
    val mana = Color(0xFF3C8FE0)
    val health = Color(0xFF4CAF50)
    val divider = Color(0x1FFFFFFF)
    val chip = Color(0xFF3A3D44)
}

/** Body of the section picked by a chip: no card and no title, the chip above already names it. */
@Composable
internal fun SectionBody(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        content = content,
    )
}

/** Titled part of a section, e.g. "Notes 3" of an ability or "Bad against 20" in matchups. Always open: one section is on the screen at a time, so there is room. */
@Composable
internal fun SubSection(
    title: String,
    count: Int?,
    titleColor: Color = AppTheme.color.textColor,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(top = 14.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, style = AppTheme.style.H5, fontWeight = FontWeight.Bold, color = titleColor)
            if (count != null) {
                HSpace(6.dp)
                Text(count.toString(), style = AppTheme.style.body, color = AppTheme.color.textSecondaryColor)
            }
            if (trailing != null) {
                HSpace(8.dp)
                trailing()
            }
        }
        content()
    }
}

/** Small uppercase caption, the way the game labels values in tooltips. */
@Composable
internal fun Caption(text: String, modifier: Modifier = Modifier, color: Color = AppTheme.color.textSecondaryColor) {
    Text(
        text = text.uppercase(),
        style = TextStyle(fontSize = 10.sp, letterSpacing = 0.8.sp, fontWeight = FontWeight.Medium),
        color = color,
        modifier = modifier,
    )
}

@Composable
internal fun VSpace(height: Dp) = androidx.compose.foundation.layout.Spacer(Modifier.height(height))

@Composable
internal fun HSpace(width: Dp) = androidx.compose.foundation.layout.Spacer(Modifier.width(width))

@Composable
internal fun InfoDivider(modifier: Modifier = Modifier) = Box(
    modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(InfoColors.divider),
)

/** Text from the parser may contain `<b>` and `<font color='#rrggbb'>`, everything else is plain. */
@Composable
internal fun RichText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = AppTheme.style.H5,
    color: Color = AppTheme.color.textColor,
    maxLines: Int = Int.MAX_VALUE,
) {
    val annotated = remember(text) { text.toAnnotatedString() }
    Text(
        text = annotated,
        style = style.copy(lineHeight = style.fontSize * 1.4f),
        color = color,
        maxLines = maxLines,
        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

private val tagRegex = Regex("""<(/?)(b|font)(?:\s+color=['"]?(#[0-9a-fA-F]{6})['"]?)?\s*>""", RegexOption.IGNORE_CASE)

internal fun String.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
    var position = 0
    var openStyles = 0
    tagRegex.findAll(this@toAnnotatedString).forEach { match ->
        append(this@toAnnotatedString.substring(position, match.range.first))
        position = match.range.last + 1
        val isClosing = match.groupValues[1] == "/"
        when {
            isClosing && openStyles > 0 -> {
                pop()
                openStyles--
            }
            isClosing -> Unit
            match.groupValues[2].equals("b", ignoreCase = true) -> {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                openStyles++
            }
            else -> {
                val color = match.groupValues[3].takeIf { it.isNotEmpty() }?.let { Color(android.graphics.Color.parseColor(it)) }
                pushStyle(SpanStyle(color = color ?: Color.Unspecified, fontWeight = FontWeight.Bold))
                openStyles++
            }
        }
    }
    append(this@toAnnotatedString.substring(position))
    repeat(openStyles) { pop() }
}

internal fun Double.format(): String {
    val rounded = (this * 100).roundToLong() / 100.0
    return if (rounded == rounded.roundToLong().toDouble()) rounded.roundToLong().toString() else rounded.toString()
}
