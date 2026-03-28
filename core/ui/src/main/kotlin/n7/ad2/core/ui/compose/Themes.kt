package n7.ad2.core.ui.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

object AppTheme {
    val style: n7.ad2.core.ui.compose.AppTypography
        @Composable get() = _root_ide_package_.n7.ad2.core.ui.compose.LocalAppTypography.current

    val color: n7.ad2.core.ui.compose.AppColor
        @Composable get() = _root_ide_package_.n7.ad2.core.ui.compose.LocalAppColor.current

    val shape: n7.ad2.core.ui.compose.AppShape
        @Composable get() = _root_ide_package_.n7.ad2.core.ui.compose.LocalAppShape.current
}

@Composable
fun AppTheme(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val appColor = if (isDarkTheme) _root_ide_package_.n7.ad2.core.ui.compose.appColorDark else _root_ide_package_.n7.ad2.core.ui.compose.appColorLight
    val appTypography = _root_ide_package_.n7.ad2.core.ui.compose.appTypography
    val appShape = _root_ide_package_.n7.ad2.core.ui.compose.appShape
    CompositionLocalProvider(
        _root_ide_package_.n7.ad2.core.ui.compose.LocalAppTypography provides appTypography,
        _root_ide_package_.n7.ad2.core.ui.compose.LocalAppColor provides appColor,
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
                !isDarkTheme,
            ),
            typography = Typography(),
            shapes = Shapes(
                small = appShape.small,
                medium = appShape.medium,
                large = appShape.large,
            ),
        ) {
            content()
        }
    }
}
