package ad2.n7.news.internal

import ad2.n7.news.internal.di.DaggerNewsComponent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import n7.ad2.android.findDependencies
import n7.ad2.ktx.viewModel
import n7.ad2.ui.ComposeView
import javax.inject.Inject

internal class SingleNewsFragment : Fragment() {

    companion object {
        fun getInstance() = SingleNewsFragment()
    }

    @Inject lateinit var singleNewsViewModelFactory: SingleNewsViewModel.Factory
    private val viewModel: SingleNewsViewModel by viewModel { singleNewsViewModelFactory.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerNewsComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView { SingleNewsScreen(viewModel) }
    }

}

@Composable
internal fun SingleNewsScreen(viewModel: SingleNewsViewModel) {
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