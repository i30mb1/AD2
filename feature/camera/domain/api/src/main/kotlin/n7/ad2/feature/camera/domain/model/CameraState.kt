package n7.ad2.feature.camera.domain.model

import n7.ad2.feature.camera.domain.Rotation

data class CameraState(
    val image: Image? = null,
    val detectedFaceNormalized: DetectedFaceNormalized? = null,
    val streamerFps: Int = 0,
    val rotation: Rotation? = null,
    val occlusion: Float = 0f,
    val blurriness: Float = 0f,
    val brightness: Float = 0f,
)
