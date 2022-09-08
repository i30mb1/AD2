package n7.ad2.ui.compose

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColor(
    val primary: Color,
    val background: Color,
    val textColor: Color,
)

val appColorLight = AppColor(
    primary = Color(0xFF448AFF),
    background = Color(0xFF202225),
    textColor = Color(0xFFFFFFFF),
)

val appColorDark = AppColor(
    primary = Color(0xFFF44336),
    background = Color(0xFFFFFFFF),
    textColor = Color(0xF0000000),
)


val LocalColor = staticCompositionLocalOf<AppColor> { error("No colors provided") }