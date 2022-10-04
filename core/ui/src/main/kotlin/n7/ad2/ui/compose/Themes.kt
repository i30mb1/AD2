package n7.ad2.ui.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

object AppTheme {
    val style: AppTypography
        @Composable get() = LocalAppTypography.current

    val color: AppColor
        @Composable get() = LocalAppColor.current
}

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val appColor = if (true) appColorDark else appColorLight
    val appTypography = appTypography
    CompositionLocalProvider(
        LocalAppTypography provides appTypography,
        LocalAppColor provides appColor,
    ) {
        MaterialTheme(
            colors = Colors(
                primary = appColor.primary,
                primaryVariant = appColor.primary,
                secondary = appColor.primary,
                secondaryVariant = appColor.primary,
                background = appColor.background,
                surface = appColor.surface,
                error = appColor.error,
                onPrimary = appColor.textColor,
                onSecondary = appColor.textColor,
                onBackground = appColor.textColor,
                onSurface = appColor.textColor,
                onError = appColor.textColor,
                !isDarkTheme
            ),
            typography = Typography(

            ),
        ) {
            content()
        }
    }
}