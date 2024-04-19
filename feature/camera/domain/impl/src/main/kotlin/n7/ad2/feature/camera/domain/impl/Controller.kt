package n7.ad2.feature.camera.domain.impl

import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
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
import n7.ad2.feature.camera.domain.impl.model.setDetectedObject
import n7.ad2.feature.camera.domain.model.CameraState
import n7.ad2.feature.camera.domain.model.Image

class Controller(
    private val previewer: Previewer,
    private val processor: Processor,
    streamer: Streamer,
    lifecycle: LifecycleOwner,
) {

    private val _state: MutableStateFlow<CameraState> = MutableStateFlow(CameraState.empty())
    val state: Flow<CameraState> = _state.asStateFlow()
    private var scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FIT_CENTER
    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    init {
        streamer.stream
            .onEach { image: Image ->
                val processorState = processor.analyze(image)
                _state.setDetectedObject(
                    processorState.detectedFace,
                    scaleType,
                    viewWidth,
                    viewHeight,
                    image.metadata.width,
                    image.metadata.height,
                    image.metadata.isImageFlipped,
                )
//                _state.setImage(image)
            }
            .flowWithLifecycle(lifecycle.lifecycle, Lifecycle.State.RESUMED)
            .launchIn(lifecycle.lifecycleScope)
    }

    fun onUIBind(
        surfaceProvider: Preview.SurfaceProvider,
        scaleType: PreviewView.ScaleType,
    ) {
        this.scaleType = scaleType
        previewer.start(surfaceProvider)
    }

    fun updatePosition(viewHeight: Int, viewWidth: Int) {
        this.viewWidth = viewWidth
        this.viewHeight = viewHeight
    }
}
