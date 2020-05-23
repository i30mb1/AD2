package n7.ad2.ui.heroInfo

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.annotation.RawRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource.HttpDataSourceException
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.google.android.exoplayer2.util.Util
import n7.ad2.R
import java.io.IOException


class AudioExoPlayer(private val application: Application) : Player.EventListener, LifecycleObserver {

    private lateinit var exoPlayer: SimpleExoPlayer

    init {
        initializePlayer()
        initAudioFocus()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_IDLE -> {
            }
            ExoPlayer.STATE_BUFFERING -> {
            }
            ExoPlayer.STATE_READY -> {
            }
            ExoPlayer.STATE_ENDED -> {
            }
            else -> {
            }
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        if (error.type == ExoPlaybackException.TYPE_SOURCE) {
            val cause: IOException = error.sourceException
            if (cause is HttpDataSourceException) {
                // An HTTP error occurred.
                // This is the request for which the error occurred.
                val requestDataSpec = cause.dataSpec
                // It's possible to find out more about the error both by casting and by
                // querying the cause.
                if (cause is InvalidResponseCodeException) {
                    // Cast to InvalidResponseCodeException and retrieve the response code,
                    // message and headers.
                } else {
                    // Try calling httpError.getCause() to retrieve the underlying cause,
                    // although note that it may be null.
                }
            }
        }
    }

    fun play(@RawRes id: Int) = play(RawResourceDataSource.buildRawResourceUri(id))

    fun play(url: String?) = play(Uri.parse(url))

    fun play(uri: Uri) {
        val source = buildMediaSource(uri)

        exoPlayer.prepare(source)
        exoPlayer.playWhenReady = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            exoPlayer.playWhenReady // play/pause state using
            exoPlayer.currentPosition // current playback position
            exoPlayer.currentWindowIndex // current window index

            exoPlayer.removeListener(this)
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            exoPlayer.removeListener(this)
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    private fun initializePlayer() {
        val trackSelector = DefaultTrackSelector(application)
        val loadControl = DefaultLoadControl()

        exoPlayer = SimpleExoPlayer.Builder(application)
                .setTrackSelector(trackSelector)
                .setLoadControl(loadControl)
                .build()

        exoPlayer.addListener(this)
    }

    private fun initAudioFocus() {
        val audioAttributes = com.google.android.exoplayer2.audio.AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_SPEECH)
                .build()

        exoPlayer.setAudioAttributes(audioAttributes, true)
    }

    private fun buildMediaSource(uri: Uri): ProgressiveMediaSource {
        val userAgent = Util.getUserAgent(application, application.getString(R.string.app_name))
        val dataSourceFactory = DefaultDataSourceFactory(application, userAgent)
        return ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi(playerView: View) {
        playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

}