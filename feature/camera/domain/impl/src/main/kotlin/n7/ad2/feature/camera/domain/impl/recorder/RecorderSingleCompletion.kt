package n7.ad2.feature.camera.domain.impl.recorder

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import n7.ad2.feature.camera.domain.Recorder
import n7.ad2.feature.camera.domain.RecorderState

class RecorderSingleCompletion(
    private val recorder: Recorder,
) : Recorder by recorder {

    @Volatile private var completedOnce = false

    override val state: Flow<RecorderState> = recorder.state.onEach { state ->
        if (state is RecorderState.Completed) completedOnce = true
    }

    override fun startOnce() {
        if (completedOnce) return
        recorder.startOnce()
    }

    override fun stop() {
        completedOnce = false
        recorder.stop()
    }
}
