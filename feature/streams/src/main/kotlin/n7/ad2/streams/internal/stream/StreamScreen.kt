package n7.ad2.streams.internal.stream

import android.graphics.Rect
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
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
import n7.ad2.streams.R

@UnstableApi
@Composable
fun StreamScreen(
    uri: String,
    isPipIconVisible: Boolean,
    onPipClicked: () -> Unit,
    onPipLayoutChanged: (Rect) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (uri.isNotBlank()) {
            Box {
                VideoPlayer(uri, onPipLayoutChanged)
                if (isPipIconVisible) Icon(
                    painterResource(id = R.drawable.pip),
                    null,
                    Modifier
                        .padding(12.dp)
                        .size(24.dp)
                        .clickable { onPipClicked() }
                        .align(Alignment.TopEnd),
                    Color.White,
                )
            }
        }
    }
}

@UnstableApi
@Composable
fun VideoPlayer(uri: String, onPipLayoutChanged: (Rect) -> Unit) {
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