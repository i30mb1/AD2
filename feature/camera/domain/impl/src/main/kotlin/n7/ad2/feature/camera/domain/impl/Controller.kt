package n7.ad2.feature.camera.domain.impl

import androidx.camera.core.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import java.io.File
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.feature.camera.domain.Previewer
import n7.ad2.feature.camera.domain.Processor
import n7.ad2.feature.camera.domain.Recorder
import n7.ad2.feature.camera.domain.RecorderState
import n7.ad2.feature.camera.domain.Streamer
import n7.ad2.feature.camera.domain.model.CameraState
import n7.ad2.feature.camera.domain.model.StreamerState

class Controller(
    private val previewer: Previewer,
    private val processor: Processor,
    private val recorder: Recorder,
    private val streamer: Streamer,
    private val lifecycle: CameraLifecycle,
    private val cameraProvider: CameraProvider,
) {

    private val _state: MutableStateFlow<CameraState> = MutableStateFlow(CameraState())
    val state: Flow<CameraState> = _state.asStateFlow()

    private var streamerJob: Job? = null

    private fun runStreamer() {
        if (streamerJob != null) return
        streamerJob = streamer.stream
            .buffer(1, BufferOverflow.DROP_OLDEST)
            .onEach { state: StreamerState ->
                val processorState = processor.analyze(state.image)
                _state.value = CameraState(
                    processorState.image,
                    processorState.detectedFaceNormalized,
                    state.fps,
                )
            }
            .flowWithLifecycle(lifecycle.lifecycle, Lifecycle.State.RESUMED)
            .launchIn(lifecycle.lifecycleScope)
    }

    fun onUIBind(surfaceProvider: Preview.SurfaceProvider) {
        val previewerUseCase = previewer.start(surfaceProvider)
        runStreamer()
        lifecycle.onUiShown()
    }

    fun onUiUnBind() {
        cameraProvider.unbind()
    }

    suspend fun startRecording(): File {
        streamerJob?.cancel()
        streamerJob = null
        recorder.startOnce()
        return recorder.state.filterIsInstance<RecorderState.Completed>().last().file
    }

    fun onDestroyView() {
        lifecycle.onUiHidden()
    }
}
