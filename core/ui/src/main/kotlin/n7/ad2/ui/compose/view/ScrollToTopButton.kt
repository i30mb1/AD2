package n7.ad2.ui.compose.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import n7.ad2.ui.compose.AppTheme

@Composable
inline fun ScrollToTopButton(
    isVisible: Boolean,
    state: LazyListState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
        modifier = modifier
            .padding(bottom = 60.dp),
    ) {
        Box(modifier = Modifier
            .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
            .background(color = AppTheme.color.primary)
            .padding(start = 8.dp, end = 8.dp, top = 2.dp, bottom = 2.dp)
            .clickable { scope.launch { state.animateScrollToItem(0) } }) {
            Icon(
                modifier = modifier
                    .rotate(180f)
                    .align(Alignment.Center),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "scroll to top",
            )
        }
    }
}

@Preview
@Composable
private fun ScrollToTopButtonPreview() {
    ScrollToTopButton(
        true,
        rememberLazyListState(),
    )
}