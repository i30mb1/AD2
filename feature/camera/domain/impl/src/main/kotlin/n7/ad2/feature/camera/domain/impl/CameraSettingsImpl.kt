package n7.ad2.feature.camera.domain.impl

import androidx.camera.core.CameraSelector
import n7.ad2.feature.camera.domain.CameraAspectRatio
import n7.ad2.feature.camera.domain.CameraSettings

class CameraSettingsImpl(
    override val isFrontCamera: Boolean = true,
    override val isDebug: Boolean = true,
    override val aspectRatio: CameraAspectRatio = CameraAspectRatio.RATIO_16_9,
    override val isIlluminationEnabled: Boolean = true,
    override val isHeadPoseEnabled: Boolean = true,
    override val isBlurrinessEnabled: Boolean = true,
) : CameraSettings

fun CameraSettings.cameraSelector(): CameraSelector = if (isFrontCamera) {
    CameraSelector.DEFAULT_FRONT_CAMERA
} else {
    CameraSelector.DEFAULT_BACK_CAMERA
}
