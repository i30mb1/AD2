package n7.ad2.games.internal.games.show

import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * A voice line from the wiki: loaded while the round is introduced, played when the question opens.
 * Call from the main thread: the listeners come back on it.
 */
internal class VoicePlayer {

    private var player: MediaPlayer? = null

    /** @return false - no sound (no internet, a broken link) */
    suspend fun prepare(url: String): Boolean {
        release()
        val media = MediaPlayer()
        player = media
        return suspendCancellableCoroutine { continuation ->
            media.setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build())
            media.setOnPreparedListener { if (continuation.isActive) continuation.resume(true) }
            media.setOnErrorListener { _, _, _ ->
                if (continuation.isActive) continuation.resume(false)
                true
            }
            continuation.invokeOnCancellation { release() }
            runCatching {
                media.setDataSource(url)
                media.prepareAsync()
            }.onFailure { if (continuation.isActive) continuation.resume(false) }
        }
    }

    /** From the start; [onDone] when the line is over */
    fun play(onDone: () -> Unit) {
        val media = player ?: return onDone()
        media.setOnCompletionListener { onDone() }
        runCatching {
            media.seekTo(0)
            media.start()
        }.onFailure { onDone() }
    }

    fun stop() {
        runCatching { player?.takeIf { it.isPlaying }?.pause() }
    }

    fun release() {
        player?.release()
        player = null
    }
}
