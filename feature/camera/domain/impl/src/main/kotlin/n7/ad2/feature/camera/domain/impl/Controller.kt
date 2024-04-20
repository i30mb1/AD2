package n7.ad2.feature.camera.domain.impl

import androidx.camera.core.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.feature.camera.domain.Previewer
import n7.ad2.feature.camera.domain.Processor
import n7.ad2.feature.camera.domain.Streamer
import n7.ad2.feature.camera.domain.model.CameraState
import n7.ad2.feature.camera.domain.model.Image
import n7.ad2.feature.camera.domain.model.ProcessorState

class Controller(
    private val previewer: Previewer,
    private val processor: Processor,
    streamer: Streamer,
    lifecycle: LifecycleOwner,
) {

    private val _state: MutableStateFlow<CameraState> = MutableStateFlow(CameraState())
    val state: Flow<CameraState> = _state.asStateFlow()

    init {
        streamer.stream
            .onEach { image: Image ->
                val processorState: ProcessorState = processor.analyze(image)
//                _state.setFace(processorState.detectedFaceNormalized)
//                _state.setImage(processorState.image)
            }
            .flowWithLifecycle(lifecycle.lifecycle, Lifecycle.State.RESUMED)
            .launchIn(lifecycle.lifecycleScope)
    }

    fun onUIBind(surfaceProvider: Preview.SurfaceProvider) {
        previewer.start(surfaceProvider)
    }
}
