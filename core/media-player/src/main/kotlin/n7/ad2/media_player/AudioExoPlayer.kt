package n7.ad2.media_player

import android.app.Application
import android.net.Uri
import androidx.annotation.RawRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.AssetDataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@UnstableApi
class AudioExoPlayer @AssistedInject constructor(
    private val context: Application,
    @Assisted private val lifecycle: Lifecycle,
) : Player.Listener, DefaultLifecycleObserver {

    @AssistedFactory
    interface Factory {
        fun create(lifecycle: Lifecycle): AudioExoPlayer
    }

    private lateinit var exoPlayer: ExoPlayer
    var playerStateListener: (playerState: PlayerState) -> Unit = { }

    sealed class PlayerState {
        object Ended : PlayerState()
        data class Error(val error: PlaybackException) : PlayerState()
    }

    init {
        lifecycle.addObserver(this)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_IDLE -> Unit
            ExoPlayer.STATE_BUFFERING -> Unit
            ExoPlayer.STATE_READY -> Unit
            ExoPlayer.STATE_ENDED -> playerStateListener(PlayerState.Ended)
            else -> Unit
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        playerStateListener.invoke(PlayerState.Error(error))
    }

    override fun onStart(owner: LifecycleOwner) {
        initializePlayer()
    }

    override fun onStop(owner: LifecycleOwner) {
        destroy()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (::exoPlayer.isInitialized) exoPlayer.removeListener(this)
        lifecycle.removeObserver(this)
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(context).build()
        exoPlayer.addListener(this)
        exoPlayer.setHandleAudioBecomingNoisy(true)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
            .build()
        exoPlayer.setAudioAttributes(audioAttributes, true)
    }

    fun playFromAssets(url: String) {
        val dataSpec = DataSpec(Uri.parse(url))

        val assetDataSource = AssetDataSource(context)
        assetDataSource.open(dataSpec)

        play(assetDataSource.uri!!)
    }

    fun play(@RawRes id: Int) {
        play(RawResourceDataSource.buildRawResourceUri(id))
    }

    fun play(url: String) {
        play(Uri.parse(url))
    }

    private fun play(uri: Uri) {
        val source = buildMediaSource(uri)

        exoPlayer.setMediaSource(source)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    private fun stop() {
        exoPlayer.stop()
    }

    private fun destroy() {
        exoPlayer.stop()
        exoPlayer.release()
    }

    private fun buildMediaSource(uri: Uri): ProgressiveMediaSource {
        val dataSourceFactory = DefaultDataSource.Factory(context)
        val mediaItem = MediaItem.fromUri(uri)
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
    }

}