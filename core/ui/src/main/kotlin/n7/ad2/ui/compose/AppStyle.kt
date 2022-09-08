package n7.ad2.ui.compose

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

data class AppStyle(
    val H5: TextStyle,
)

val appStyle = AppStyle(
    H5 = TextStyle(
        fontSize = 14.sp,
    ),
)

val LocalAppStyle = staticCompositionLocalOf<AppStyle> { error("No colors provided") }