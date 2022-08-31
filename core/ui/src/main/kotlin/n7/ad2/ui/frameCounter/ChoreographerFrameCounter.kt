package n7.ad2.ui.frameCounter

import android.os.Handler
import android.os.Looper
import android.view.Choreographer
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import n7.ad2.ktx.lazyUnsafe

class ChoreographerFrameCounter(
    lifecycle: Lifecycle?,
) : FrameCounter, Choreographer.FrameCallback, DefaultLifecycleObserver {

    override var isJunkCallback: ((isJunk: Boolean) -> Unit)? = null
    override var fpsCallback: ((fps: Int) -> Unit)? = null
    private var count = 0
    private val mainHandler by lazyUnsafe { Handler(Looper.getMainLooper()) }
    private val choreographer by lazyUnsafe { Choreographer.getInstance() }
    private val runnable = Runnable { calculate() }

    init {
        lifecycle?.addObserver(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        count++
        choreographer.postFrameCallback(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        choreographer.postFrameCallback(this)
        mainHandler.postDelayed(runnable, FrameCounter.FPS_INTERVAL_TIME)
    }

    override fun onPause(owner: LifecycleOwner) {
        choreographer.removeFrameCallback(this)
        mainHandler.removeCallbacks(runnable)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        fpsCallback = null
    }

    private fun calculate() {
        fpsCallback?.invoke(count)
        count = 0
        mainHandler.postDelayed(runnable, FrameCounter.FPS_INTERVAL_TIME)
    }

}