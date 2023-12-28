package n7.ad2.feature.camera.domain.impl

import androidx.camera.core.CameraSelector
import n7.ad2.feature.camera.domain.CameraSettings

class CameraSettingsImpl(
    override val isFrontCamera: Boolean = true,
    override val isDebug: Boolean = true,
) : CameraSettings

fun CameraSettings.cameraSelector(): CameraSelector {
    return if (isFrontCamera) {
        CameraSelector.DEFAULT_FRONT_CAMERA
    } else {
        CameraSelector.DEFAULT_BACK_CAMERA
    }
}
