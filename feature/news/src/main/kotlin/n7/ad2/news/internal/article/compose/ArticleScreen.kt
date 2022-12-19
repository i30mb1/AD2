package n7.ad2.news.internal.article.compose

import android.webkit.WebView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import n7.ad2.news.internal.article.ArticleViewModel

@Composable
internal fun ArticleScreen(viewModel: ArticleViewModel) {
    val state = viewModel.state.collectAsState().value
    when {
        state.isLoading -> Unit
        else -> Browser(state.href)
    }
}

@Composable
private fun Browser(href: String) {
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
                loadUrl(href)
            }
        },
        update = { it.loadUrl(href) }
    )
}