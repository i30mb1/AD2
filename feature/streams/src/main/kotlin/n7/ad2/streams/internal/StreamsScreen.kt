package n7.ad2.streams.internal

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import n7.ad2.android.DrawerPercentListener
import n7.ad2.streams.R
import n7.ad2.streams.internal.domain.vo.VOStream
import n7.ad2.ui.compose.AppTheme
import n7.ad2.ui.compose.view.ErrorScreen
import n7.ad2.ui.compose.view.LoadingScreen

@Composable
internal fun StreamsScreen(
    streams: LazyPagingItems<VOStream>,
    drawerPercentListener: DrawerPercentListener,
    onStreamClicked: (stream: VOStream) -> Unit,
) {
    Crossfade(targetState = streams.loadState.refresh) { state ->
        when (state) {
            is LoadState.Loading -> LoadingScreen()
            is LoadState.Error -> ErrorScreen(state.error) { streams.refresh() }
            else -> StreamsList(streams, onStreamClicked, drawerPercentListener)
        }
    }
}

@Composable
internal fun StreamsList(
    streams: LazyPagingItems<VOStream>,
    onStreamClicked: (stream: VOStream) -> Unit,
    drawerPercentListener: DrawerPercentListener,
    modifier: Modifier = Modifier,
) {
    var contentComposed by remember { mutableStateOf(false) }
    var drawerPercent: Float by remember { mutableStateOf(0f) }
    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(top = 4.dp + insetsTop * drawerPercent, start = 4.dp, end = 4.dp),
    ) {
        items(streams) { stream: VOStream? ->
            contentComposed = true
            when (stream) {
                is VOStream.Simple -> SimpleStream(stream, onStreamClicked = onStreamClicked)
                else -> Unit
            }
        }
        if (streams.loadState.append is LoadState.Loading) {
            item {
                Box(modifier = Modifier
                    .size(100.dp)
                    .background(Color.Red)) {}
            }
        }
    }
    ReportDrawnWhen { contentComposed }
    DisposableEffect(key1 = Unit) {
        drawerPercentListener.setDrawerPercentListener { percent -> drawerPercent = percent }
        onDispose { drawerPercentListener.setDrawerPercentListener(null) }
    }
}

@Preview
@Composable
internal fun SimpleStream(
    @PreviewParameter(PreviewStreamProvider::class) stream: VOStream.Simple,
    modifier: Modifier = Modifier,
    onStreamClicked: (stream: VOStream) -> Unit = { },
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .clickable { onStreamClicked(stream) }
            .background(AppTheme.color.surface)
    ) {
        Row {
            Box {
                AsyncImage(
                    modifier = Modifier.size(160.dp, 90.dp),
                    model = stream.imageUrl,
                    contentDescription = stream.title,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.stream_placeholder),
                )
                Image(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(12.dp),
                    painter = painterResource(id = R.drawable.ic_stream_viewers),
                    contentDescription = null,
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    style = AppTheme.style.H5,
                    color = AppTheme.color.textColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    text = stream.streamerName,
                )
                Text(
                    style = AppTheme.style.body,
                    color = AppTheme.color.textSecondaryColor,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    text = stream.title,
                )
            }

        }
    }
}

internal class PreviewStreamProvider : PreviewParameterProvider<VOStream> {
    override val values: Sequence<VOStream> = sequenceOf(VOStream.Simple(
        "N7 vs Navi",
        "i30mb1",
        "empty",
    ))
}