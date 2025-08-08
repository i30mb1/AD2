package n7.ad2.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import n7.ad2.ui.compose.AppTheme

@Composable
fun TextWithDotsSuffix(
    text: String,
    modifier: Modifier = Modifier,
) {
    var animText by remember { mutableStateOf(text) }
    Text(
        modifier = modifier,
        text = animText,
        style = AppTheme.style.body,
        color = AppTheme.color.textColor,
    )
    LaunchedEffect(Unit) {
        var value: Byte = 0
        while (true) {
            animText = when (value % 3) {
                2 -> "$text..."
                1 -> "$text.."
                else -> "$text."
            }
            delay(500)
            if (value == Byte.MAX_VALUE) value = 0
            value++
        }
    }
}

@Preview
@Composable
fun TextWithDotsSuffixPreview() {
    AppTheme {
        TextWithDotsSuffix("Hello")
    }
}
