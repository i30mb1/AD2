package n7.ad2.media_player

import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.annotation.RawRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.AssetDataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.RawResourceDataSource

class AudioExoPlayer(
    private val context: Application,
    private val lifecycle: Lifecycle,
) : Player.Listener, DefaultLifecycleObserver {

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) initializePlayer()
    }

    override fun onResume(owner: LifecycleOwner) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) initializePlayer()
    }

    override fun onPause(owner: LifecycleOwner) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) destroy()
    }

    override fun onStop(owner: LifecycleOwner) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) destroy()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (::exoPlayer.isInitialized) exoPlayer.removeListener(this)
        lifecycle.removeObserver(this)
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(context).build()
        exoPlayer.addListener(this)
        initAudioFocus()
    }

    fun playFromAssets(url: String) {
        val dataSpec = DataSpec(Uri.parse(url))

        val assetDataSource = AssetDataSource(context)
        assetDataSource.open(dataSpec)

        play(assetDataSource.uri!!)
    }

    fun play(@RawRes id: Int) = play(RawResourceDataSource.buildRawResourceUri(id))

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

    private fun initAudioFocus() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_SPEECH)
            .build()

        exoPlayer.setAudioAttributes(audioAttributes, true)
    }

    private fun buildMediaSource(uri: Uri): ProgressiveMediaSource {
        val dataSourceFactory = DefaultDataSource.Factory(context)
        val mediaItem = MediaItem.fromUri(uri)
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
    }

}