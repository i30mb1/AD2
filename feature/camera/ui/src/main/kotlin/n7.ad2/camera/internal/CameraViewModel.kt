package n7.ad2.camera.internal

import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import n7.ad2.android.OneShotValue
import n7.ad2.app.logger.Logger
import n7.ad2.camera.internal.model.CameraStateUI
import n7.ad2.camera.internal.model.setFace
import n7.ad2.camera.internal.model.setPreviewSizes
import n7.ad2.camera.internal.model.setScaleType
import n7.ad2.camera.internal.model.updateDelayForRecording
import n7.ad2.feature.camera.domain.impl.Controller
import n7.ad2.feature.camera.domain.impl.FPSTimer
import n7.ad2.feature.camera.domain.model.CameraState

internal class CameraViewModel @AssistedInject constructor(
    private val controller: Controller,
    private val recordingDelay: RecordingDelay,
    private val logger: Logger,
) : ViewModel() {

    private val timer = FPSTimer("ViewModel fps:", logger)
    private val _state: MutableStateFlow<CameraStateUI> = MutableStateFlow(CameraStateUI())
    val state: StateFlow<CameraStateUI> = _state.asStateFlow()

    init {
        timer.timer.launchIn(viewModelScope)
        controller.state
            .onEach { cameraState: CameraState ->
                considerAboutRecording(cameraState)
                _state.setFace(
                    cameraState.detectedFaceNormalized,
                    cameraState.image,
                )
                _state.update {
                    it.copy(
//                        image = cameraState.image?.source as? Bitmap,
                        streamerFps = cameraState.streamerFps.toString(),
                    )
                }
                timer.count++
            }
            .launchIn(viewModelScope)
        recordingDelay.state
            .onEach {
                _state.updateDelayForRecording(it)
            }
            .launchIn(viewModelScope)
    }

    private fun considerAboutRecording(cameraState: CameraState) {
        if (cameraState.detectedFaceNormalized != null) {
            recordingDelay.startOnce(viewModelScope)
        } else {
            recordingDelay.stop()
        }
    }

    fun onUIBind(surfaceProvider: Preview.SurfaceProvider, scaleType: PreviewView.ScaleType) {
        _state.setScaleType(scaleType)
        viewModelScope.launch {
            controller.onUIBind(surfaceProvider)
        }
    }

    fun onUiUnBind() {
        controller.onUiUnBind()
    }

    fun onGlobalPosition(viewHeight: Int, viewWidth: Int) {
        _state.setPreviewSizes(viewHeight, viewWidth)
    }

    fun startRecording() {
        viewModelScope.launch {
            val file = controller.startRecording()
            _state.update { state ->
                state.copy(
                    recordedFile = OneShotValue(file),
                )
            }
        }
    }

    fun onDestroyView() {
        controller.onDestroyView()
    }

    @AssistedFactory
    interface Factory {
        fun create(): CameraViewModel
    }
}
