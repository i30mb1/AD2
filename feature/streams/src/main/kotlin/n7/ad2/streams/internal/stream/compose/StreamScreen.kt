@file:UnstableApi
@file:OptIn(ExperimentalMaterialApi::class)

package n7.ad2.streams.internal.stream.compose

import android.graphics.Rect
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.launch
import n7.ad2.streams.R

@UnstableApi
@Composable
internal fun StreamScreen(
    uri: String,
    isPipIconVisible: Boolean,
    onPipClicked: () -> Unit,
    onPipLayoutChanged: (Rect) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded }
    )
    val coroutineScope = rememberCoroutineScope()
    BackHandler(sheetState.isVisible) { coroutineScope.launch { sheetState.hide() } }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            Box(modifier = Modifier
                .height(300.dp)
                .background(Color.Red))

        },
        modifier = Modifier.fillMaxSize(),
        content = {
            BottomSheetContent(modifier, uri, onPipLayoutChanged, isPipIconVisible, onPipClicked) {
                coroutineScope.launch {
                    if (sheetState.isVisible) sheetState.hide()
                    else sheetState.show()
                }
            }
        }
    )

}

@Composable
private fun BottomSheetContent(
    modifier: Modifier,
    uri: String,
    onPipLayoutChanged: (Rect) -> Unit,
    isPipIconVisible: Boolean,
    onPipClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
) {
    Column(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box {
            VideoPlayer(uri, onPipLayoutChanged)
            Row(modifier = Modifier.align(Alignment.TopEnd)) {
                if (isPipIconVisible) Icon(
                    painterResource(id = R.drawable.pip),
                    null,
                    Modifier
                        .padding(6.dp)
                        .size(24.dp)
                        .clickable { onPipClicked() },
                    Color.White,
                )
                Icon(
                    painterResource(R.drawable.ic_settings),
                    null,
                    Modifier
                        .padding(6.dp)
                        .size(24.dp)
                        .clickable { onSettingsClicked() },
                    Color.White,
                )
            }
        }
    }
}

@UnstableApi
@Composable
internal fun VideoPlayer(uri: String, onPipLayoutChanged: (Rect) -> Unit) {
    val context = LocalContext.current
    val statusBarInsets = WindowInsets.statusBars.getTop(LocalDensity.current)
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            playWhenReady = true
        }
    }

    AndroidView(
        modifier = Modifier
            .heightIn(min = 200.dp)
            .fillMaxWidth(),
        factory = {
            PlayerView(context).apply {
                hideController()
                useController = false
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
                    onPipLayoutChanged(Rect(left, top + statusBarInsets, right, bottom))
                }
            }
        },
        update = {
            val dataSource = DefaultHttpDataSource.Factory()
            val mediaSource = HlsMediaSource.Factory(dataSource).createMediaSource(MediaItem.fromUri(uri.toUri()))
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()
        })
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }
}

@UnstableApi
@Preview
@Composable
private fun StreamScreenPreview() {
    StreamScreen(
        "", true, {}, {}
    )
}