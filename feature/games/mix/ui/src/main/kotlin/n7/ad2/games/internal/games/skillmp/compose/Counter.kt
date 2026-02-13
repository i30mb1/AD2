package n7.ad2.games.internal.games.skillmp.compose

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import n7.ad2.ui.compose.AppTheme

@Preview
@Composable
private fun SimpleCounter() {
    Counter(count = 1)
}

@Composable
internal fun Counter(count: Int, modifier: Modifier = Modifier) {
    Text(
        text = count.toString(),
        style = AppTheme.style.H5.copy(fontSize = 40.sp),
        color = AppTheme.color.textColor,
        modifier = modifier,
    )
}
