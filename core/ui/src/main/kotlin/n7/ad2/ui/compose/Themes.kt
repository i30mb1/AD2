package n7.ad2.ui.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

object AppTheme {
    val style: AppStyle
        @Composable get() = LocalAppStyle.current

    val color: AppColor
        @Composable get() = LocalColor.current
}

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (isDarkTheme) appColorDark else appColorLight
    CompositionLocalProvider(
        LocalAppStyle provides appStyle,
        LocalColor provides colors,
        content = content,
    )
}