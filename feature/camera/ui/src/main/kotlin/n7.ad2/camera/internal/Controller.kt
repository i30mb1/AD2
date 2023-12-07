package n7.ad2.camera.internal

import androidx.camera.core.Preview
import androidx.lifecycle.coroutineScope
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.jetbrains.kotlinx.dl.api.inference.objectdetection.DetectedObject

class Controller @Inject constructor(
    private val previewer: Previewer,
    private val processing: Processing,
    private val streaming: Streaming,
    private val lifecycle: CameraLifecycle,
) {

    private val _state: MutableStateFlow<CameraState> = MutableStateFlow(CameraState.empty())
    val state: Flow<CameraState> = _state.asStateFlow()

    init {
        streaming.stream
            .onEach { bitmap ->
                val processorState = processing.analyze(bitmap)
                _state.setDetectedObject(processorState.detectedObject)
            }
            .launchIn(lifecycle.lifecycle.coroutineScope)
    }

    suspend fun onUIBind(surfaceProvider: Preview.SurfaceProvider) {
        previewer.start(surfaceProvider)
        streaming.start()
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
