package n7.ad2.news.ui.internal.screen.news.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import n7.ad2.android.DrawerPercentListener
import n7.ad2.core.ui.compose.view.FilterChip
import n7.ad2.core.ui.compose.view.ScrollToTopFab
import n7.ad2.feature.news.ui.R
import n7.ad2.news.ui.internal.screen.news.NewsViewModel
import n7.ad2.news.ui.internal.screen.news.model.NewsFilter
import n7.ad2.news.ui.internal.screen.news.model.NewsVO

private const val JUMP_INDEX = 20

@Composable
internal fun NewsScreen(viewModel: NewsViewModel, drawerPercentListener: DrawerPercentListener?, onNewsClicked: (newsID: Int) -> Unit, modifier: Modifier = Modifier) {
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val insetsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    // without the drawer (demo app) the screen is always fully open
    var drawerPercent by remember { mutableFloatStateOf(if (drawerPercentListener == null) 1f else 0f) }
    val isFiltersScrolledAway by remember { derivedStateOf { state.firstVisibleItemIndex > 0 } }
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val news: LazyPagingItems<NewsVO> = viewModel.news.collectAsLazyPagingItems()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = state,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = 6.dp + insetsTop * drawerPercent,
                bottom = 16.dp + insetsBottom * drawerPercent,
            ),
        ) {
            item(key = "filters") {
                Filters(selected = filter, onSelect = { selected ->
                    viewModel.setFilter(selected)
                    scope.launch { state.scrollToItem(0) }
                })
            }
            items(news.itemCount) { index: Int ->
                val item = news[index] ?: return@items
                if (index == 0) FeaturedNewsItem(item, onNewsClicked) else NewsItem(item, onNewsClicked)
            }
        }
        ScrollToTopFab(
            isVisible = isFiltersScrolledAway,
            onClick = {
                scope.launch {
                    if (state.firstVisibleItemIndex > JUMP_INDEX) state.scrollToItem(JUMP_INDEX)
                    state.animateScrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomStart),
        )
    }
    DisposableEffect(drawerPercentListener) {
        val listener: (Float) -> Unit = { percent -> drawerPercent = percent }
        drawerPercentListener?.setDrawerPercentListener(listener)
        onDispose { drawerPercentListener?.removeDrawerPercentListener(listener) }
    }
}

@Composable
private fun Filters(selected: NewsFilter, onSelect: (NewsFilter) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        FilterChip(stringResource(R.string.news_filter_all), selected == NewsFilter.ALL, onClick = { onSelect(NewsFilter.ALL) })
        FilterChip(stringResource(R.string.news_filter_official), selected == NewsFilter.OFFICIAL, onClick = { onSelect(NewsFilter.OFFICIAL) })
        FilterChip(stringResource(R.string.news_filter_press), selected == NewsFilter.PRESS, onClick = { onSelect(NewsFilter.PRESS) })
    }
}
