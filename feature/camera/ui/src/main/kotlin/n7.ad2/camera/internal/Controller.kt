package n7.ad2.camera.internal

import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import n7.ad2.feature.camera.domain.Previewer
import n7.ad2.feature.camera.domain.Processor
import n7.ad2.feature.camera.domain.Streamer
import n7.ad2.feature.camera.domain.model.Image
import org.jetbrains.kotlinx.dl.api.inference.objectdetection.DetectedObject

class Controller @Inject constructor(
    private val previewer: Previewer,
    private val processor: Processor,
    private val streamer: Streamer,
    private val lifecycle: LifecycleOwner,
) {

    private val _state: MutableStateFlow<CameraState> = MutableStateFlow(CameraState.empty())
    val state: Flow<CameraState> = _state.asStateFlow()

    init {
        streamer.stream
            .onEach { image: Image ->
                val processorState = processor.analyze(image)
                _state.setDetectedObject(processorState.detectedObject)
            }
            .launchIn(lifecycle.lifecycleScope)
    }

    suspend fun onUIBind(surfaceProvider: Preview.SurfaceProvider) {
        previewer.start(surfaceProvider)
        streamer.start()
    }
}

data class CameraState(
    val detectedObject: DetectedObject?,
) {

    companion object {
        fun empty() = CameraState(
            null,
        )
    }
}

internal fun MutableStateFlow<CameraState>.setDetectedObject(detectedObject: DetectedObject?) = update {
    it.copy(
        detectedObject = detectedObject
    )
}
