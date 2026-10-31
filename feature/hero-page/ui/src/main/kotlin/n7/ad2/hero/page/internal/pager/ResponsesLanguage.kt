package n7.ad2.hero.page.internal.pager

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import n7.ad2.AppLocale
import n7.ad2.core.ui.compose.AppTheme

/**
 * Language of the replies, switched in the banner next to the hero name. The replies tab is a fragment of its own,
 * it reports the language it shows and takes the picks, the same way the banner fold reaches the tabs.
 */
internal class ResponsesLanguage {
    var current by mutableStateOf<AppLocale?>(null)
    var onPick: (AppLocale) -> Unit by mutableStateOf({})
}

internal val LocalResponsesLanguage = staticCompositionLocalOf<ResponsesLanguage?> { null }

@Composable
internal fun LocaleToggle(locale: AppLocale, onLocaleChange: (AppLocale) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(AppTheme.color.surface)
            .padding(2.dp),
    ) {
        listOf(AppLocale.Russian, AppLocale.English).forEach { option ->
            val isSelected = option == locale
            Box(
                modifier = Modifier
                    .height(28.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AppTheme.color.primary else Color.Transparent)
                    .clickable(role = Role.RadioButton) { if (!isSelected) onLocaleChange(option) }
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = option.value.uppercase(),
                    style = AppTheme.style.body,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else AppTheme.color.textSecondaryColor,
                )
            }
        }
    }
}
