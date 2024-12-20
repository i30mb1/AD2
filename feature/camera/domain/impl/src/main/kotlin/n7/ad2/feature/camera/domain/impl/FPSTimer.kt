package n7.ad2.feature.camera.domain.impl

import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import n7.ad2.app.logger.Logger

class FPSTimer(
    private val message: String,
    private val logger: Logger,
) {
    var count = 0
    var latestFps = 0
    val timer = ticker(1.seconds.inWholeMilliseconds)
        .consumeAsFlow()
        .onEach {
            logger.log("$message: $count")
            latestFps = count
            count = 0
        }
}
