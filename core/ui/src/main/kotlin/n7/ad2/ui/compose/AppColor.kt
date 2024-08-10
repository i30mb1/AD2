package n7.ad2.ui.compose

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColor(
    val primary: Color,
    val background: Color,
    val surface: Color,
    val textColor: Color,
    val textSecondaryColor: Color,
    val error: Color,
    val backgroundMyServer: Color = Color(0xFF8BC34A),
)

val appColorLight = AppColor(
    primary = Color(0xFF448AFF),
    background = Color(0xFF202225),
    surface = Color(0xFF2F3136),
    textColor = Color(0xFFFFFFFF),
    textSecondaryColor = Color(0xFFC1C2C3),
    error = Color(0xFFFF0000),
)

val appColorDark = AppColor(
    primary = Color(0xFFF44336),
    background = Color(0xFF202225),
    surface = Color(0xFF2F3136),
    textColor = Color(0xFFFFFFFF),
    textSecondaryColor = Color(0xFFC1C2C3),
    error = Color(0xF0FF0000),
)

val LocalAppColor = staticCompositionLocalOf { appColorLight }