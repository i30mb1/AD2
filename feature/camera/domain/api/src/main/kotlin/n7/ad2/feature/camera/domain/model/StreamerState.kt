package n7.ad2.feature.camera.domain.model

data class StreamerState(
    val image: Image?,
    val fps: Int,
)

class Image(
    val source: Any,
    val metadata: ImageMetadata,
)

data class ImageMetadata(
    val width: Int,
    val height: Int,
    val isImageFlipped: Boolean,
)
