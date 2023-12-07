package n7.ad2.camera.internal

import androidx.camera.core.CameraSelector

val settings = CameraSettings()

class CameraSettings(
    val isFrontCamera: Boolean = true,
    val isDebug: Boolean = true,
)

fun CameraSettings.cameraSelector(): CameraSelector {
    return if (isFrontCamera) {
        CameraSelector.DEFAULT_FRONT_CAMERA
    } else {
        CameraSelector.DEFAULT_BACK_CAMERA
    }
}
