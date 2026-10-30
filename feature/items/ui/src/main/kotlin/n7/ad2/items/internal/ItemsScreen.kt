package n7.ad2.items.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import n7.ad2.android.DrawerPercentListener
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.compose.Bold
import n7.ad2.core.ui.compose.view.GridHeader
import n7.ad2.core.ui.compose.view.ScrollToTopFab
import n7.ad2.core.ui.compose.view.SearchField
import n7.ad2.feature.items.ui.R
import n7.ad2.items.internal.model.ItemUI

private const val SPAN_COUNT = 4
private const val JUMP_INDEX = 40
private const val ITEM_IMAGE_RATIO = 88f / 64f

@Composable
internal fun ItemsScreen(
    items: List<ItemUI>,
    query: String,
    onQueryChange: (query: String) -> Unit,
    onItemClick: (item: ItemUI.Body) -> Unit,
    drawerPercentListener: DrawerPercentListener?,
) {
    val state = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    // без шторки (например в demo) экран всегда раскрыт, поэтому сразу отступаем от системных панелей
    var drawerPercent by remember { mutableFloatStateOf(if (drawerPercentListener == null) 1f else 0f) }
    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val insetsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val isSearchScrolledAway by remember { derivedStateOf { state.firstVisibleItemIndex > 0 } }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(SPAN_COUNT),
            state = state,
            contentPadding = PaddingValues(
                start = 3.dp,
                end = 3.dp,
                top = 6.dp + insetsTop * drawerPercent,
                bottom = 6.dp + insetsBottom * drawerPercent,
            ),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            item(key = "search", span = { GridItemSpan(maxLineSpan) }, contentType = "search") {
                SearchField(query = query, onQueryChange = onQueryChange, hint = stringResource(R.string.search_item))
            }
            items.forEachIndexed { index, item ->
                when (item) {
                    is ItemUI.Header -> item(key = "header_${item.data.title}", span = { GridItemSpan(maxLineSpan) }, contentType = "header") {
                        GridHeader(item.data.title)
                    }
                    is ItemUI.Body -> item(key = "${index}_${item.name}", contentType = "item") {
                        ItemCard(item, onItemClick)
                    }
                }
            }
        }
        ScrollToTopFab(
            isVisible = isSearchScrolledAway,
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
private fun ItemCard(item: ItemUI.Body, onItemClick: (item: ItemUI.Body) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ITEM_IMAGE_RATIO)
            .clip(AppTheme.shape.small)
            .background(AppTheme.color.surface)
            .clickable { onItemClick(item) },
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = item.imageUrl,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.item_placeholder),
        )
        Text(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Color(0x25000000))
                .padding(horizontal = 4.dp),
            text = item.title,
            style = AppTheme.style.body.copy(fontSize = 10.sp).Bold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
