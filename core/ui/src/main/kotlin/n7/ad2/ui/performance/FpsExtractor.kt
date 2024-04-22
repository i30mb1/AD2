package n7.ad2.ui.performance

import android.view.Choreographer
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ui.frameCounter.FrameCounter

/**
 * Считаем сколько кадров было отрисовано за 1 секунду
 */
internal class FpsExtractor(
    lifecycle: Lifecycle?,
) : FrameCounter, Choreographer.FrameCallback, DefaultLifecycleObserver {

    override var isJunkCallback: ((isJunk: Boolean) -> Unit)? = null
    override var fpsCallback: ((fps: Int) -> Unit)? = null
    private var frameCount = 0
    private var startTime: Long = 0
    private var endTime: Long = 0
    private val choreographer by lazyUnsafe { Choreographer.getInstance() }

    init {
        lifecycle?.addObserver(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (frameCount++ == 0) {
            startTime = frameTimeNanos
        }
        endTime = frameTimeNanos
        choreographer.postFrameCallback(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        choreographer.postFrameCallback(this)
    }

    override fun onPause(owner: LifecycleOwner) {
        choreographer.removeFrameCallback(this)
    }

    // FPS = 1s * frameCount / (end - start)
    fun get(): Int {
        val fps = when (frameCount) {
            0 -> 0
            else -> {
                val averageFrameDuration = (endTime - startTime) / frameCount
                if (averageFrameDuration == 0L) 0 else (NANOS_IN_SECOND / averageFrameDuration).toInt()
            }
        }
        frameCount = 0
        startTime = 0
        endTime = 0

        return fps
    }

    private companion object {
        const val NANOS_IN_SECOND: Long = 1_000_000_000L
    }
}
