package n7.ad2.core.ui.compose

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

data class AppShape(val small: CornerBasedShape, val medium: CornerBasedShape, val large: CornerBasedShape)

val appShape = _root_ide_package_.n7.ad2.core.ui.compose.AppShape(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp),
)

val LocalAppShape = staticCompositionLocalOf { _root_ide_package_.n7.ad2.core.ui.compose.appShape }
