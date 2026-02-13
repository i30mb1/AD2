package n7.ad2.news.ui.internal.screen.article.compose

import android.webkit.WebView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import n7.ad2.news.ui.internal.screen.article.ArticleViewModel
import n7.ad2.ui.compose.AppTheme
import okhttp3.internal.toHexString

@Composable
internal fun ArticleScreen(viewModel: ArticleViewModel) {
    val state = viewModel.state.collectAsState().value
    when {
        state.isLoading -> Unit
        else -> Browser(state.body)
    }
}

@Composable
private fun Browser(body: String) {
    val isDark = isSystemInDarkTheme()
    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val insetsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val textColor = AppTheme.color.textColor.toArgb().toHexString()
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(top = insetsTop, bottom = insetsBottom),
        factory = { context ->
            WebView(context).apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                isVerticalScrollBarEnabled = false
                settings.textZoom = 100
                settings.defaultFontSize = 14
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && isDark) {
                    WebSettingsCompat.setForceDark(this.settings, WebSettingsCompat.FORCE_DARK_ON)
                }
                loadBody(body, textColor)
            }
        },
        update = {
            it.loadBody(body, textColor)
        },
    )
}

private fun WebView.loadBody(body: String, textColor: String) {
    loadDataWithBaseURL(
        null,
        "<style>img{display: inline;height: auto;max-width: 100%;text-decoration:none;}</style><style type=\"text/css\">body{color:#$textColor;}</style>$body",
        "text/html",
        "UTF-8",
        null,
    )
}
