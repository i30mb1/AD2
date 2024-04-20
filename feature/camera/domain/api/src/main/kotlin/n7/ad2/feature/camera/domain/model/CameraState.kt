package n7.ad2.feature.camera.domain.model


data class CameraState(
    val image: Image? = null,
    val detectedFaceNormalized: DetectedFaceNormalized? = null,
)
