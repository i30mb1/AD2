package n7.ad2.streams.internal

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import n7.ad2.android.DrawerPercentListener
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.core.ui.compose.view.FilterChip
import n7.ad2.core.ui.compose.view.LoadingAnimation
import n7.ad2.core.ui.compose.view.ScrollToTopFab
import n7.ad2.feature.streams.R
import n7.ad2.streams.internal.common.rememberNowMillis
import n7.ad2.streams.internal.common.uptimeText
import n7.ad2.streams.internal.common.viewersText
import n7.ad2.streams.internal.domain.vo.VOStream
import n7.ad2.streams.internal.model.StreamsLanguage

private const val JUMP_INDEX = 20
private const val IMAGE_RATIO = 16f / 9f
private val scrimColor = Color.Black.copy(alpha = 0.65f)
private val tagBorderColor = Color(0xFF3A3D43)

@Composable
internal fun StreamsScreen(
    streams: LazyPagingItems<VOStream>,
    language: StreamsLanguage,
    drawerPercentListener: DrawerPercentListener,
    onLanguageSelected: (StreamsLanguage) -> Unit,
    onStreamClicked: (stream: VOStream.Simple) -> Unit,
) {
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val insetsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    var drawerPercent by remember { mutableFloatStateOf(0f) }
    val isFiltersScrolledAway by remember { derivedStateOf { state.firstVisibleItemIndex > 0 } }
    val now = rememberNowMillis()
    val refresh = streams.loadState.refresh

    Box(modifier = Modifier.fillMaxSize()) {
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
                Filters(selected = language, onSelect = { selected ->
                    onLanguageSelected(selected)
                    scope.launch { state.scrollToItem(0) }
                })
            }
            when {
                refresh is LoadState.Loading && streams.itemCount == 0 -> item(key = "loading") {
                    Box(Modifier.fillParentMaxSize(0.8f), contentAlignment = Alignment.Center) { LoadingAnimation() }
                }

                refresh is LoadState.Error -> item(key = "error") {
                    Box(Modifier.fillParentMaxSize(0.8f)) { ErrorScreen(refresh.error) { streams.retry() } }
                }

                else -> items(streams.itemCount) { index: Int ->
                    val stream = streams[index] as? VOStream.Simple ?: return@items
                    if (index == 0) TopStream(stream, now, onStreamClicked) else StreamItem(stream, now, onStreamClicked)
                }
            }
            if (streams.loadState.append is LoadState.Loading) {
                item(key = "append") {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { LoadingAnimation() }
                }
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
    ReportDrawnWhen { streams.itemCount > 0 }
    DisposableEffect(drawerPercentListener) {
        val listener: (Float) -> Unit = { percent -> drawerPercent = percent }
        drawerPercentListener.setDrawerPercentListener(listener)
        onDispose { drawerPercentListener.removeDrawerPercentListener(listener) }
    }
}

@Composable
private fun Filters(selected: StreamsLanguage, onSelect: (StreamsLanguage) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        FilterChip(stringResource(R.string.streams_filter_all), selected == StreamsLanguage.ALL, onClick = { onSelect(StreamsLanguage.ALL) })
        FilterChip(stringResource(R.string.streams_filter_russian), selected == StreamsLanguage.RUSSIAN, onClick = { onSelect(StreamsLanguage.RUSSIAN) })
        FilterChip(stringResource(R.string.streams_filter_english), selected == StreamsLanguage.ENGLISH, onClick = { onSelect(StreamsLanguage.ENGLISH) })
    }
}

/** The most watched stream: wide picture with the badges on it, the name and the title under it. */
@Composable
private fun TopStream(stream: VOStream.Simple, now: Long, onStreamClicked: (VOStream.Simple) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppTheme.shape.medium)
            .background(AppTheme.color.surface)
            .clickable { onStreamClicked(stream) },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(IMAGE_RATIO),
        ) {
            Thumbnail(stream, Modifier.fillMaxSize())
            LiveBadge(Modifier.padding(8.dp).align(Alignment.TopStart))
            ViewersBadge(stream.viewers, Modifier.padding(8.dp).align(Alignment.BottomStart))
            val uptime = uptimeText(stream.startedAtMillis, now)
            if (uptime.isNotEmpty()) {
                Text(
                    text = uptime,
                    style = AppTheme.style.body,
                    color = Color.White,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomEnd)
                        .clip(AppTheme.shape.small)
                        .background(scrimColor)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }
        Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stream.displayName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
                LanguageTag(stream.language, Modifier.padding(start = 8.dp))
            }
            Text(
                text = stream.title,
                style = AppTheme.style.H5,
                color = AppTheme.color.textSecondaryColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

/** Every other stream: small picture on the left, name, title and how long it is live. */
@Composable
private fun StreamItem(stream: VOStream.Simple, now: Long, onStreamClicked: (VOStream.Simple) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppTheme.shape.medium)
            .background(AppTheme.color.surface)
            .clickable { onStreamClicked(stream) }
            .padding(8.dp),
    ) {
        Box(
            modifier = Modifier
                .width(128.dp)
                .aspectRatio(IMAGE_RATIO)
                .clip(AppTheme.shape.small),
        ) {
            Thumbnail(stream, Modifier.fillMaxSize())
            ViewersBadge(stream.viewers, Modifier.padding(4.dp).align(Alignment.BottomStart))
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stream.displayName,
                    style = AppTheme.style.H5,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.color.textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                LanguageTag(stream.language, Modifier.padding(start = 6.dp))
            }
            Text(
                text = stream.title,
                style = AppTheme.style.body,
                color = AppTheme.color.textSecondaryColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )
            val uptime = uptimeText(stream.startedAtMillis, now)
            if (uptime.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.streams_on_air, uptime),
                    fontSize = 11.sp,
                    color = AppTheme.color.textSecondaryColor.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun Thumbnail(stream: VOStream.Simple, modifier: Modifier) {
    AsyncImage(
        model = stream.imageUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(R.drawable.stream_placeholder),
        modifier = modifier.background(AppTheme.color.background),
    )
}

@Composable
internal fun LiveBadge(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.streams_live),
        style = AppTheme.style.body,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        color = Color.White,
        modifier = modifier
            .clip(AppTheme.shape.small)
            .background(AppTheme.color.primary)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

@Composable
private fun ViewersBadge(viewers: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(AppTheme.shape.small)
            .background(scrimColor)
            .padding(horizontal = 5.dp, vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(AppTheme.color.primary),
        )
        Text(
            text = viewersText(viewers),
            style = AppTheme.style.body,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun LanguageTag(language: String, modifier: Modifier = Modifier) {
    if (language.isEmpty()) return
    Text(
        text = language.uppercase(),
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = AppTheme.color.textSecondaryColor,
        modifier = modifier
            .border(1.dp, tagBorderColor, AppTheme.shape.small)
            .padding(horizontal = 4.dp),
    )
}

@Preview
@Composable
private fun StreamItemPreview() {
    val stream = VOStream.Simple("BetBoom Dacha — плей-офф | Team Spirit vs Tundra", "dota2ti_ru", "", 48_200, "dota2ti_ru", "ru", 0)
    AppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TopStream(stream, 0, onStreamClicked = {})
            StreamItem(stream, 0, onStreamClicked = {})
        }
    }
}
