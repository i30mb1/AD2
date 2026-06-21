package n7.ad2.feature.camera.domain.impl.recorder

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import n7.ad2.app.logger.Logger
import n7.ad2.feature.camera.domain.Recorder
import n7.ad2.feature.camera.domain.RecorderState

class RecorderWithLogging(
    private val recorder: Recorder,
    private val logger: Logger,
) : Recorder by recorder {

    override val state: Flow<RecorderState> = recorder.state.onEach { state ->
        logger.log("Recorder state: $state")
    }

    override fun startOnce() {
        logger.log("Recorder startOnce")
        recorder.startOnce()
    }

    override fun stop() {
        logger.log("Recorder stop")
        recorder.stop()
    }
}
