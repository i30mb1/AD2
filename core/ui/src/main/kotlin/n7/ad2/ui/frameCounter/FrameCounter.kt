package n7.ad2.ui.frameCounter

interface FrameCounter {

    companion object {
        const val FPS_INTERVAL_TIME = 1000L
    }

    var fpsCallback: ((fps: Int) -> Unit)?
    var isJunkCallback: ((isJunk: Boolean) -> Unit)?
}