package n7.ad2.ui.heroPage

import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.annotation.RawRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.AssetDataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.google.android.exoplayer2.util.Util
import n7.ad2.R

interface Playable {
    val isPlaying: MutableLiveData<Boolean>
    val audioUrl: String?
}

class AudioExoPlayer(
    private val application: Application,
    private val lifecycle: Lifecycle,
    private var listener: ((exception: Exception) -> Unit)? = null,
) : Player.EventListener, LifecycleObserver {

    private lateinit var exoPlayer: SimpleExoPlayer
    private var isPlaying = MutableLiveData(false)

    init {
        lifecycle.addObserver(this)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_IDLE -> Unit
            ExoPlayer.STATE_BUFFERING -> Unit
            ExoPlayer.STATE_READY -> Unit
            ExoPlayer.STATE_ENDED -> isPlaying.value = false
            else -> isPlaying.value = false
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        listener?.invoke(error)
        isPlaying.value = false
    }

    fun playFromAssets(url: String) {
        val dataSpec = DataSpec(Uri.parse(url))

        val assetDataSource = AssetDataSource(application)
        assetDataSource.open(dataSpec)

        play(assetDataSource.uri!!)
    }

    fun play(model: Playable) {
        if (isPlaying !== model.isPlaying) stop()
        isPlaying = model.isPlaying
        if (isPlaying.value!!) stop() else play(model.audioUrl!!)
    }

    fun play(@RawRes id: Int) = play(RawResourceDataSource.buildRawResourceUri(id))

    fun play(url: String) = play(Uri.parse(url))

    fun play(uri: Uri) {
        isPlaying.value = true
        val source = buildMediaSource(uri)

        exoPlayer.prepare(source)
        exoPlayer.playWhenReady = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onStart() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) initializePlayer() else Unit

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) initializePlayer() else Unit

    private fun initializePlayer() {
        exoPlayer = SimpleExoPlayer.Builder(application).build()

        exoPlayer.addListener(this)
        initAudioFocus()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) destroy() else Unit

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onStop() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) destroy() else Unit

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        if (::exoPlayer.isInitialized) exoPlayer.removeListener(this)
        lifecycle.removeObserver(this)
    }

    private fun stop() {
        isPlaying.value = false
        exoPlayer.stop()
    }

    private fun destroy() {
        exoPlayer.playWhenReady // play/pause state using
        exoPlayer.currentPosition // current playback position
        exoPlayer.currentWindowIndex // current window index

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
        val userAgent = Util.getUserAgent(application, application.getString(R.string.app_name))
        val dataSourceFactory = DefaultDataSourceFactory(application, userAgent)

        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }

}