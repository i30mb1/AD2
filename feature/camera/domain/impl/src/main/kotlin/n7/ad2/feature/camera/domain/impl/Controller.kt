package n7.ad2.feature.camera.domain.impl

import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.core.UseCaseGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import java.io.File
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
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
    private val logger: Logger,
    private val lifecycle: CameraLifecycle,
    private val cameraProvider: CameraProvider,
    private val dispatchersProvider: DispatchersProvider,
) {

    private val timer = FPSTimer("Controller fps:", logger)
    private val _state: MutableStateFlow<CameraState> = MutableStateFlow(CameraState())
    val state: Flow<CameraState> = _state.combine(timer.timer) { state, _ -> state }

    private var streamerJob: Job? = null

    private fun runStreamer() {
        if (streamerJob != null) return
        streamerJob = streamer.stream
            .onEach { state: StreamerState ->
                val processorState = processor.analyze(state.image)
                _state.value = CameraState(
                    processorState.image,
                    processorState.detectedFaceNormalized,
                    state.fps,
                )
                timer.count++
            }
            .flowWithLifecycle(lifecycle.lifecycle, Lifecycle.State.RESUMED)
            .flowOn(dispatchersProvider.IO)
            // lifecycle.lifecycleScope использует main поток, так что надо переключаться
            .launchIn(lifecycle.lifecycleScope)
    }

    fun onUIBind(surfaceProvider: Preview.SurfaceProvider) {
        lifecycle.onUiShown()
        val previewerUseCase = previewer.start(surfaceProvider) as UseCase
        val streamerUseCase = streamer.start() as UseCase
        val recorderUseCase = recorder.init() as UseCase
        val useCaseGroup = UseCaseGroup.Builder()
            .addUseCase(previewerUseCase)
            .addUseCase(streamerUseCase)
            .addUseCase(recorderUseCase)
            .build()
        cameraProvider.bind(useCaseGroup)
        runStreamer()
    }

    fun onUiUnBind() {
        cameraProvider.unbind()
    }

    suspend fun startRecording(): File {
        streamerJob?.cancel()
        streamerJob = null
        recorder.startOnce()
        return recorder.state.filterIsInstance<RecorderState.Completed>().first().file
    }

    fun onDestroyView() {
        lifecycle.onUiHidden()
    }
}
