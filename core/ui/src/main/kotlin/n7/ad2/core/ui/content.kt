package n7.ad2.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import n7.ad2.core.ui.compose.AppTheme

fun Fragment.content(body: @Composable () -> Unit): ComposeView = content { _root_ide_package_.n7.ad2.core.ui.compose.AppTheme { body() } }
