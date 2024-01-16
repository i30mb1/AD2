package n7.ad2.camera.internal

import androidx.camera.core.Preview
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
                        image = cameraState.image
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onUIBind(surfaceProvider: Preview.SurfaceProvider) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            controller.onUIBind(surfaceProvider)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(): CameraViewModel
    }
}
