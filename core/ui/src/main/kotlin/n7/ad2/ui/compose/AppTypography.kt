package n7.ad2.ui.compose

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class AppTypography(
    val H3: TextStyle,
    val H4: TextStyle,
    val H5: TextStyle,
    val body: TextStyle,
)

val appTypography = AppTypography(
    H3 = TextStyle(
        fontSize = 18.sp,
    ),
    H4 = TextStyle(
        fontSize = 16.sp,
    ),
    H5 = TextStyle(
        fontSize = 14.sp,
    ),
    body = TextStyle(
        fontSize = 12.sp,
    ),
)

val LocalAppTypography = staticCompositionLocalOf { appTypography }

val TextStyle.Bold
    get() = copy(fontWeight = FontWeight.Bold)
