package n7.ad2.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import n7.ad2.ui.compose.AppTheme

fun Fragment.ComposeView(content: @Composable () -> Unit): ComposeView {
    val view = ComposeView(requireContext())
    view.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
    view.setContent {
        AppTheme {
            content()
        }
    }
    return view
}