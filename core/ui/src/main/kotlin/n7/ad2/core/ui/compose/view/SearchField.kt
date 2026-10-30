package n7.ad2.core.ui.compose.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.R
import n7.ad2.core.ui.compose.AppTheme

/**
 * Строка поиска в стиле карточек приложения, задумана как первый элемент списка
 */
@Composable
fun SearchField(query: String, onQueryChange: (query: String) -> Unit, hint: String, modifier: Modifier = Modifier) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
        shape = AppTheme.shape.small,
        color = AppTheme.color.surface,
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = AppTheme.color.primary,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (query.isEmpty()) {
                    Text(text = hint, style = AppTheme.style.H5, color = AppTheme.color.textSecondaryColor, maxLines = 1)
                }
                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = AppTheme.style.H5.copy(color = AppTheme.color.textColor),
                    cursorBrush = SolidColor(AppTheme.color.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        },
                    ),
                )
            }
            if (query.isNotEmpty()) {
                IconButton(modifier = Modifier.size(36.dp), onClick = { onQueryChange("") }) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.desc_clear_search),
                        tint = AppTheme.color.textColor,
                    )
                }
            }
        }
    }
}

@Suppress("UnusedPrivateMember")
@Preview
@Composable
private fun SearchFieldPreview() {
    AppTheme {
        SearchField(query = "", onQueryChange = {}, hint = "Search")
    }
}
