package n7.ad2.feature.camera.domain.impl

import android.util.Log
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import n7.ad2.app.logger.Logger
import kotlin.time.Duration.Companion.seconds

class FPSTimer(private val logger: Logger?) {
    val counter = FPSCounter(0, 0, 0)
    val timer = ticker(1.seconds.inWholeMilliseconds)
        .consumeAsFlow()
        .onEach {
            logger?.log("$counter")
            if (logger == null) {
                Log.d("N7", "$counter")
            }
            counter.clear()
        }
}

data class FPSCounter(var streamer: Int, var analyzer: Int, var ui: Int) {
    fun clear() {
        streamer = 0
        analyzer = 0
        ui = 0
    }
}
