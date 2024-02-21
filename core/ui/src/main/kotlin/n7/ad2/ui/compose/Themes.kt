package n7.ad2.ui.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

object AppTheme {
    val style: AppTypography
        @Composable get() = LocalAppTypography.current

    val color: AppColor
        @Composable get() = LocalAppColor.current

    val shape: AppShape
        @Composable get() = LocalAppShape.current
}

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val appColor = if (isDarkTheme) appColorDark else appColorLight
    val appTypography = appTypography
    val appShape = appShape
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
            shapes = Shapes(
                small = appShape.small,
                medium = appShape.medium,
                large = appShape.large,
            )
        ) {
            content()
        }
    }
}