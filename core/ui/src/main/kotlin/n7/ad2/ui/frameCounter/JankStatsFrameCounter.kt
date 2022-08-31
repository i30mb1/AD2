package n7.ad2.ui.frameCounter

import android.os.Handler
import android.os.Looper
import android.view.Window
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.metrics.performance.JankStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import n7.ad2.ktx.lazyUnsafe

class JankStatsFrameCounter(
    lifecycle: Lifecycle?,
    window: Window,
) : FrameCounter, DefaultLifecycleObserver {

    override var isJunkCallback: ((isJunk: Boolean) -> Unit)? = null
    override var fpsCallback: ((fps: Int) -> Unit)? = null

    //    private var count = 0
    private var isJunk = false
    private val mainHandler by lazyUnsafe { Handler(Looper.getMainLooper()) }
    private val runnable = Runnable { calculate() }
    private val listener = JankStats.OnFrameListener { data ->
        isJunk = data.isJank
//        count++
    }

    init {
        lifecycle?.addObserver(this)
        JankStats.createAndTrack(window, Dispatchers.Default.asExecutor(), listener)
    }

    override fun onResume(owner: LifecycleOwner) {
        mainHandler.postDelayed(runnable, FrameCounter.FPS_INTERVAL_TIME)
    }

    override fun onPause(owner: LifecycleOwner) {
        mainHandler.removeCallbacks(runnable)
    }

    private fun calculate() {
        isJunkCallback?.invoke(isJunk)
//        fpsCallback?.invoke(count)
//        count = 0
        mainHandler.postDelayed(runnable, FrameCounter.FPS_INTERVAL_TIME)
    }

}
