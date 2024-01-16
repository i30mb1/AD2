package n7.ad2.camera.internal

import android.graphics.Bitmap
import androidx.camera.core.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
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
    lifecycle: LifecycleOwner,
) {

    private val _state: MutableStateFlow<CameraState> = MutableStateFlow(CameraState.empty())
    val state: Flow<CameraState> = _state.asStateFlow()

    init {
        streamer.stream
            .onEach { image: Image ->
                val processorState = processor.analyze(image)
                _state.setDetectedObject(processorState.detectedObject)
                _state.setBitmap(image.source as Bitmap)
            }
            .flowWithLifecycle(lifecycle.lifecycle, Lifecycle.State.RESUMED)
            .launchIn(lifecycle.lifecycleScope)
    }

    suspend fun onUIBind(surfaceProvider: Preview.SurfaceProvider) {
        previewer.start(surfaceProvider)
    }
}

data class CameraState(
    val detectedObject: DetectedObject?,
    val image: Bitmap?,
) {

    companion object {
        fun empty() = CameraState(
            null,
            null,
        )
    }
}

internal fun MutableStateFlow<CameraState>.setDetectedObject(detectedObject: DetectedObject?) = update {
    it.copy(
        detectedObject = detectedObject
    )
}

internal fun MutableStateFlow<CameraState>.setBitmap(image: Bitmap) = update {
    it.copy(
        image = image
    )
}
