package n7.ad2.camera.internal

import androidx.camera.core.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class CameraViewModel @AssistedInject constructor(
    private val controller: Controller,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): CameraViewModel
    }

    fun onUIBind(surfaceProvider: Preview.SurfaceProvider) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            controller.onUIBind(surfaceProvider)
        }
    }
}
