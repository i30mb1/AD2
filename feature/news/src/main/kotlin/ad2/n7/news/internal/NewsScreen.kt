@file:OptIn(ExperimentalMaterialApi::class)

package ad2.n7.news.internal

import ad2.n7.news.internal.domain.model.NewsVO
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import n7.ad2.android.DrawerPercentListener
import n7.ad2.ui.compose.AppTheme
import n7.ad2.ui.compose.bounceClick
import n7.ad2.ui.compose.view.ScrollToTopButton

@Composable
internal fun NewsScreen(
    viewModel: NewsViewModel,
    drawerPercentListener: DrawerPercentListener,
    onNewsClicked: (newsID: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
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
            items(news) { item: NewsVO? -> if (item != null) NewsItem(item, onNewsClicked) }
        }

        ScrollToTopButton(showScrollToTopButton, state)
    }
    DisposableEffect(key1 = Unit) {
        drawerPercentListener.setDrawerPercentListener { percent -> drawerPercent = percent }
        onDispose { drawerPercentListener.setDrawerPercentListener(null) }
    }
}

@Composable
internal fun NewsItem(item: NewsVO, onNewsClicked: (newsID: Int) -> Unit) {
    Surface(
        modifier = Modifier
            .height(170.dp)
            .bounceClick()
            .clip(RoundedCornerShape(6.dp)),
        color = AppTheme.color.surface,
        elevation = 4.dp,
        onClick = { onNewsClicked(item.id) },
    ) {
        AsyncImage(model = item.image.origin, contentDescription = item.title, contentScale = ContentScale.Crop)
        Text(
            modifier = Modifier
                .background(Color(0x4D000000))
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 32.dp),
            textAlign = TextAlign.Center,
            text = item.title,
            style = AppTheme.style.H5,
            color = Color.White,
        )
    }
}