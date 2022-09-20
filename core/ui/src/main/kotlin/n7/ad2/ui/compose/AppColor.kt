package n7.ad2.ui.compose

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColor(
    val primary: Color,
    val background: Color,
    val surface: Color,
    val textColor: Color,
    val error: Color,
)

val appColorLight = AppColor(
    primary = Color(0xFF448AFF),
    background = Color(0xFF202225),
    surface = Color(0xFF2F3136),
    textColor = Color(0xFFFFFFFF),
    error = Color(0xFFFF0000),
)

val appColorDark = AppColor(
    primary = Color(0xFFF44336),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    textColor = Color(0xF0000000),
    error = Color(0xF0FF0000),
)

val LocalAppColor = staticCompositionLocalOf { appColorLight }