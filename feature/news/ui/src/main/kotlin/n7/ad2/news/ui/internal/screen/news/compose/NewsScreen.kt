package n7.ad2.news.ui.internal.screen.news.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import n7.ad2.android.DrawerPercentListener
import n7.ad2.news.ui.internal.screen.news.NewsViewModel
import n7.ad2.news.ui.internal.screen.news.model.NewsVO
import n7.ad2.ui.compose.view.ScrollToTopButton

@Composable
internal fun NewsScreen(viewModel: NewsViewModel, drawerPercentListener: DrawerPercentListener, onNewsClicked: (newsID: Int) -> Unit, modifier: Modifier = Modifier) {
    val state = rememberLazyListState()

    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    var drawerPercent by remember { mutableStateOf(0f) }
    val showScrollToTopButton by remember { derivedStateOf { state.firstVisibleItemIndex > 0 } }
    val news: LazyPagingItems<NewsVO> = viewModel.news.collectAsLazyPagingItems()
    Box {
        LazyColumn(
            modifier = modifier,
            state = state,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 4.dp + insetsTop * drawerPercent, start = 4.dp, end = 4.dp),
        ) {
            items(news.itemCount) { index: Int ->
                val item = news[index]
                if (item != null) NewsItem(item, onNewsClicked)
            }
        }
        ScrollToTopButton(showScrollToTopButton, state, Modifier.align(Alignment.BottomEnd))
    }
    DisposableEffect(key1 = Unit) {
        drawerPercentListener.setDrawerPercentListener { percent -> drawerPercent = percent }
        onDispose { drawerPercentListener.setDrawerPercentListener(null) }
    }
}
