package n7.ad2.hero.page.internal.pager

import android.view.OrientationEventListener
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.AppLocale
import n7.ad2.core.ui.compose.AppTheme

@Composable
internal fun HeroPageToolbar(
    heroName: String,
    locale: AppLocale,
    currentPage: Int,
    onLocaleChange: (AppLocale) -> Unit,
) {
    var rotation by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val listener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation != ORIENTATION_UNKNOWN) rotation = 360f - orientation
            }
        }
        listener.enable()
        onDispose { listener.disable() }
    }
    TopAppBar(
        title = { Text(text = heroName, style = AppTheme.style.H4, color = AppTheme.color.textColor) },
        backgroundColor = AppTheme.color.surface,
        navigationIcon = {
            AnimatedVisibility(visible = currentPage == 0) {
                AsyncImage(
                    model = "file:///android_asset/heroes/$heroName/minimap.png",
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(30.dp)
                        .rotate(rotation),
                )
            }
        },
        actions = {
            AnimatedVisibility(visible = currentPage == 1) {
                TextButton(onClick = {
                    val next = if (locale is AppLocale.Russian) AppLocale.English else AppLocale.Russian
                    onLocaleChange(next)
                }) {
                    AnimatedContent(targetState = locale.value, label = "locale") { Text(it) }
                }
            }
        },
    )
}
