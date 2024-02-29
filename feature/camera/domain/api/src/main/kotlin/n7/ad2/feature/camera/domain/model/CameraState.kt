package n7.ad2.feature.camera.domain.model

import org.jetbrains.kotlinx.dl.api.inference.objectdetection.DetectedObject

data class CameraState(
    val detectedObject: DetectedObject?,
    val raw: raw?,
) {

    companion object {
        fun empty() = CameraState(
            null,
            null,
        )
    }
}

data class raw(
    val image: Image?,
    val detectedObject: DetectedObject?,
)
