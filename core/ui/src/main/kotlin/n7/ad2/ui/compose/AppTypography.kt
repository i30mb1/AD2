package n7.ad2.ui.compose

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

data class AppTypography(
    val H5: TextStyle,
)

val appTypography = AppTypography(
    H5 = TextStyle(
        fontSize = 14.sp,
    ),
)

val LocalAppTypography = staticCompositionLocalOf<AppTypography> { error("No colors provided") }