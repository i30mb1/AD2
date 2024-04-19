package n7.ad2.feature.camera.domain.model


data class CameraState(
    val detectedFace: DetectedFace?,
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
    val detectedFace: DetectedFace?,
)
