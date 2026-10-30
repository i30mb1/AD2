package n7.ad2.core.ui.compose.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.R
import n7.ad2.core.ui.compose.AppTheme

/**
 * Маленькая круглая кнопка в левом нижнем углу, возвращает список к самому началу
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScrollToTopFab(isVisible: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(initialScale = 0.6f),
        exit = fadeOut() + scaleOut(targetScale = 0.6f),
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(12.dp),
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = AppTheme.color.surface,
            elevation = 6.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = stringResource(R.string.desc_scroll_to_top),
                    tint = AppTheme.color.primary,
                )
            }
        }
    }
}

@Suppress("UnusedPrivateMember")
@Preview
@Composable
private fun ScrollToTopFabPreview() {
    AppTheme {
        ScrollToTopFab(isVisible = true, onClick = {})
    }
}
