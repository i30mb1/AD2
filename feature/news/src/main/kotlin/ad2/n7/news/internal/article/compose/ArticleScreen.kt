package ad2.n7.news.internal.article.compose

import ad2.n7.news.internal.article.ArticleViewModel
import android.webkit.WebView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature

@Composable
internal fun ArticleScreen(viewModel: ArticleViewModel) {
    val isDark = isSystemInDarkTheme()
    AndroidView(
        modifier = Modifier.systemBarsPadding(),
        factory = { context ->
            WebView(context).apply {
                settings.textZoom = 85
                settings.defaultFontSize = 10
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && isDark) {
                    WebSettingsCompat.setForceDark(this.settings, WebSettingsCompat.FORCE_DARK_ON)
                }
                loadUrl("https://www.dotabuff.com/blog/2022-10-04-top-tier-supports-in-the-current-meta")
            }
        },
        update = { it.loadUrl("url") }
    )
}