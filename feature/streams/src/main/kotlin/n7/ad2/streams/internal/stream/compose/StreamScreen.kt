@file:UnstableApi

package n7.ad2.streams.internal.stream.compose

import android.graphics.Rect
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.streams.R
import n7.ad2.streams.internal.LiveBadge
import n7.ad2.streams.internal.chat.ChatMessage
import n7.ad2.streams.internal.common.rememberNowMillis
import n7.ad2.streams.internal.common.uptimeText
import n7.ad2.streams.internal.common.viewersText
import n7.ad2.streams.internal.domain.vo.VOStream
import n7.ad2.streams.internal.model.ChatDensity

private const val CONTROLS_HIDE_MILLIS = 4_000L
private const val HIGH_FRAME_RATE = 50f
private val panelColor = Color(0xEB141416)
private val segmentColor = Color(0xFF2F3136)
private val qualityPanelWidth = 280.dp

/** One line of the quality list, [override] is null for the automatic choice. */
private class Quality(val label: String, val override: TrackSelectionOverride?)

@Composable
internal fun StreamScreen(
    streamerName: String,
    uri: String,
    info: VOStream.Simple?,
    chat: Flow<ChatMessage>,
    chatDensity: ChatDensity,
    isInPip: Boolean,
    isPipAvailable: Boolean,
    onChatDensityChanged: (ChatDensity) -> Unit,
    onBackClicked: () -> Unit,
    onPipClicked: () -> Unit,
    onPipLayoutChanged: (Rect) -> Unit,
) {
    val player = rememberStreamPlayer(uri)
    var tracks by remember { mutableStateOf(player.currentTracks) }
    var selected by remember { mutableStateOf<TrackSelectionOverride?>(null) }
    var areControlsVisible by remember { mutableStateOf(true) }
    var isQualityVisible by remember { mutableStateOf(false) }
    // every touch restarts the countdown, the controls stay while the quality list is open
    var lastTouch by remember { mutableStateOf(0L) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onTracksChanged(newTracks: Tracks) {
                tracks = newTracks
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }
    LaunchedEffect(areControlsVisible, isQualityVisible, lastTouch) {
        if (areControlsVisible && !isQualityVisible) {
            delay(CONTROLS_HIDE_MILLIS)
            areControlsVisible = false
        }
    }
    BackHandler(isQualityVisible) { isQualityVisible = false }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                areControlsVisible = !areControlsVisible
                lastTouch = System.currentTimeMillis()
            },
    ) {
        VideoPlayer(player, onPipLayoutChanged)
        if (isInPip) return@Box

        FlyingChat(
            messages = chat,
            density = chatDensity,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(top = 8.dp),
        )
        AnimatedVisibility(visible = areControlsVisible, enter = fadeIn(), exit = fadeOut(), modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.desc_back),
                    tint = Color.White,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(panelColor)
                        .clickable(role = Role.Button, onClick = onBackClicked)
                        .padding(8.dp),
                )
                ControlBar(
                    streamerName = streamerName,
                    info = info,
                    chatDensity = chatDensity,
                    // "АВТО" stands out as a word, the sizes keep their "p": 720p60
                    qualityLabel = qualities(tracks).find { it.override == selected && selected != null }?.label ?: stringResource(R.string.streams_quality_auto).uppercase(),
                    isPipAvailable = isPipAvailable,
                    onChatDensityChanged = { density ->
                        onChatDensityChanged(density)
                        lastTouch = System.currentTimeMillis()
                    },
                    onPipClicked = onPipClicked,
                    onQualityClicked = { isQualityVisible = true },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }
        }
        QualityPanel(
            isVisible = isQualityVisible,
            qualities = qualities(tracks),
            selected = selected,
            onDismiss = { isQualityVisible = false },
            onSelected = { quality ->
                selected = quality.override
                player.trackSelectionParameters = player.trackSelectionParameters.buildUpon().apply {
                    if (quality.override == null) clearOverridesOfType(C.TRACK_TYPE_VIDEO) else setOverrideForType(quality.override)
                }.build()
                isQualityVisible = false
            },
        )
    }
}

@Composable
private fun ControlBar(
    streamerName: String,
    info: VOStream.Simple?,
    chatDensity: ChatDensity,
    qualityLabel: String,
    isPipAvailable: Boolean,
    onChatDensityChanged: (ChatDensity) -> Unit,
    onPipClicked: () -> Unit,
    onQualityClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val now = rememberNowMillis()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(panelColor)
            // a touch on the bar must not hide the controls
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        LiveBadge()
        Text(
            text = info?.displayName ?: streamerName,
            style = AppTheme.style.H5,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
        )
        if (info != null) {
            val details = listOf(viewersText(info.viewers), uptimeText(info.startedAtMillis, now)).filter { it.isNotEmpty() }
            Text(
                text = details.joinToString(" · "),
                style = AppTheme.style.body,
                color = AppTheme.color.textSecondaryColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
        Spacer(Modifier.weight(1f))
        Text(text = stringResource(R.string.streams_chat), style = AppTheme.style.body, color = AppTheme.color.textSecondaryColor)
        DensitySwitch(chatDensity, onChatDensityChanged)
        if (isPipAvailable) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.pip),
                contentDescription = stringResource(R.string.desc_picture_in_picture),
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onPipClicked)
                    .padding(6.dp),
            )
        }
        Text(
            text = qualityLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .clip(AppTheme.shape.small)
                .border(1.dp, Color.White.copy(alpha = 0.4f), AppTheme.shape.small)
                .clickable(role = Role.Button, onClick = onQualityClicked)
                .padding(horizontal = 8.dp, vertical = 5.dp),
        )
    }
}

@Composable
private fun DensitySwitch(selected: ChatDensity, onSelected: (ChatDensity) -> Unit) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(segmentColor)
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        ChatDensity.entries.forEach { density ->
            val label = when (density) {
                ChatDensity.OFF -> R.string.streams_chat_off
                ChatDensity.LOW -> R.string.streams_chat_low
                ChatDensity.NORMAL -> R.string.streams_chat_normal
                ChatDensity.HIGH -> R.string.streams_chat_high
            }
            val isSelected = density == selected
            Text(
                text = stringResource(label),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isSelected) AppTheme.color.primary else Color.Transparent)
                    .clickable(role = Role.Tab) { onSelected(density) }
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            )
        }
    }
}

@Composable
private fun QualityPanel(isVisible: Boolean, qualities: List<Quality>, selected: TrackSelectionOverride?, onDismiss: () -> Unit, onSelected: (Quality) -> Unit) {
    AnimatedVisibility(visible = isVisible, enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss),
        )
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally { width -> width },
            exit = slideOutHorizontally { width -> width },
        ) {
            Column(
                modifier = Modifier
                    .width(qualityPanelWidth)
                    .fillMaxHeight()
                    .background(AppTheme.color.surface)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
            ) {
                Text(
                    text = stringResource(R.string.streams_quality).uppercase(),
                    style = AppTheme.style.body,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = AppTheme.color.textSecondaryColor,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp),
                )
                qualities.forEach { quality ->
                    val isSelected = quality.override == selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable(role = Role.RadioButton) { onSelected(quality) }
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = quality.label,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AppTheme.color.textColor else AppTheme.color.textSecondaryColor,
                            modifier = Modifier.weight(1f),
                        )
                        if (isSelected) Icon(Icons.Default.Check, contentDescription = null, tint = AppTheme.color.primary)
                    }
                }
            }
        }
    }
}

/** "Auto" and every video size of the stream, the biggest first: 1080p60, 720p60, 480p... */
@Composable
private fun qualities(tracks: Tracks): List<Quality> {
    val auto = Quality(stringResource(R.string.streams_quality_auto), null)
    val videos = tracks.groups
        .filter { group -> group.type == C.TRACK_TYPE_VIDEO }
        .flatMap { group ->
            (0 until group.length)
                .filter(group::isTrackSupported)
                .map { index -> group.getTrackFormat(index) to TrackSelectionOverride(group.mediaTrackGroup, index) }
        }
        .filter { (format, _) -> format.height > 0 }
        .sortedWith(compareByDescending<Pair<androidx.media3.common.Format, TrackSelectionOverride>> { it.first.height }.thenByDescending { it.first.frameRate })
        .map { (format, override) ->
            val fps = if (format.frameRate >= HIGH_FRAME_RATE) "60" else ""
            Quality("${format.height}p$fps", override)
        }
        .distinctBy { it.label }
    return listOf(auto) + videos
}

@Composable
private fun rememberStreamPlayer(uri: String): ExoPlayer {
    val context = LocalContext.current
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            playWhenReady = true
        }
    }
    LaunchedEffect(uri) {
        if (uri.isBlank()) return@LaunchedEffect
        val mediaSource = HlsMediaSource.Factory(DefaultHttpDataSource.Factory()).createMediaSource(MediaItem.fromUri(uri.toUri()))
        player.setMediaSource(mediaSource)
        player.prepare()
    }
    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                // in picture in picture the activity is paused but still on the screen, the video goes on until it is stopped
                Lifecycle.Event.ON_STOP -> player.pause()
                Lifecycle.Event.ON_START -> player.play()
                else -> Unit
            }
        }
        val lifecycle = lifecycleOwner.value.lifecycle
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            player.release()
        }
    }
    return player
}

@Composable
private fun VideoPlayer(player: ExoPlayer, onPipLayoutChanged: (Rect) -> Unit) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            PlayerView(context).apply {
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                this.player = player
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
                    onPipLayoutChanged(Rect(left, top, right, bottom))
                }
            }
        },
    )
}
