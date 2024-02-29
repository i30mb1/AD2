package n7.ad2.camera.internal

import android.graphics.Bitmap
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import n7.ad2.camera.internal.model.CameraStateUI
import n7.ad2.camera.internal.model.toDetectedRect
import n7.ad2.feature.camera.domain.impl.Controller

internal class CameraViewModel @AssistedInject constructor(
    private val controller: Controller,
) : ViewModel() {

    private val _state: MutableStateFlow<CameraStateUI> = MutableStateFlow(CameraStateUI.init())
    val state: StateFlow<CameraStateUI> = _state.asStateFlow()

    init {
        controller.state
            .onEach { cameraState ->
                _state.update {
                    it.copy(
                        image = cameraState.raw?.image?.source as? Bitmap,
                        detectedRect = cameraState.detectedObject.toDetectedRect(),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onUIBind(surfaceProvider: Preview.SurfaceProvider, scaleType: PreviewView.ScaleType) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            controller.onUIBind(surfaceProvider, scaleType)
        }
    }

    fun onGlobalPosition(viewHeight: Int, viewWidth: Int) {
        controller.updatePosition(viewHeight, viewWidth)
    }

    @AssistedFactory
    interface Factory {
        fun create(): CameraViewModel
    }
}
