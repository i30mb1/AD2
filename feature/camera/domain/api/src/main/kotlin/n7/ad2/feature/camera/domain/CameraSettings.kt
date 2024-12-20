package n7.ad2.feature.camera.domain

interface CameraSettings {
    val isFrontCamera: Boolean
    val isDebug: Boolean
    val aspectRatio: CameraAspectRatio
}

enum class CameraAspectRatio {
    RATIO_16_9,
    RATIO_4_3,
}

