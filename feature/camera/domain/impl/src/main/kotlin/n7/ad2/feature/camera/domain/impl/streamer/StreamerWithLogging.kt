package n7.ad2.feature.camera.domain.impl.streamer

import kotlinx.coroutines.flow.Flow
import n7.ad2.app.logger.Logger
import n7.ad2.feature.camera.domain.Streamer
import n7.ad2.feature.camera.domain.model.StreamerState

class StreamerWithLogging(
    private val streamer: Streamer,
    private val logger: Logger,
) : Streamer by streamer {

    override val stream: Flow<StreamerState> = streamer.stream

    override fun start(): Any {
        logger.log("Streamer started")
        return streamer.start()
    }

    override fun stop() {
        logger.log("Streamer stopped")
        streamer.stop()
    }
}
