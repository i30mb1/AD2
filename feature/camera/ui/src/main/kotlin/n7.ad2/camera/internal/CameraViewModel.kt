package n7.ad2.camera.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import n7.ad2.Resources

internal class CameraViewModel @AssistedInject constructor(
    private val res: Resources,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): CameraViewModel
    }
}
