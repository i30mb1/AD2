package n7.ad2.feature.camera.domain.impl

import androidx.camera.core.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import n7.ad2.feature.camera.domain.Previewer
import n7.ad2.feature.camera.domain.Processor
import n7.ad2.feature.camera.domain.Recorder
import n7.ad2.feature.camera.domain.Streamer
import n7.ad2.feature.camera.domain.impl.model.setFace
import n7.ad2.feature.camera.domain.impl.model.setImage
import n7.ad2.feature.camera.domain.model.CameraState
import n7.ad2.feature.camera.domain.model.Image

class Controller(
    private val previewer: Previewer,
    private val processor: Processor,
    private val recorder: Recorder,
    private val streamer: Streamer,
    private val lifecycle: LifecycleOwner,
) {

    private val _state: MutableStateFlow<CameraState> = MutableStateFlow(CameraState())
    val state: Flow<CameraState> = _state.asStateFlow()

    private var streamerJob: Job? = null

    private fun runStreamer() {
        if (streamerJob != null) return
        streamerJob = streamer.stream
            .onEach { image: Image ->
                val processorState = processor.analyze(image)
                _state.setFace(processorState.detectedFaceNormalized)
                _state.setImage(processorState.image)
            }
            .flowWithLifecycle(lifecycle.lifecycle, Lifecycle.State.RESUMED)
            .launchIn(lifecycle.lifecycleScope + Dispatchers.IO)
    }

    fun onUIBind(surfaceProvider: Preview.SurfaceProvider) {
        previewer.start(surfaceProvider)
        runStreamer()
    }

    suspend fun startRecording(): File {
        streamerJob?.cancelAndJoin()
        return recorder.start()
    }
}
