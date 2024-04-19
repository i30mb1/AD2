package n7.ad2.camera.internal.model

import android.graphics.Bitmap
import n7.ad2.feature.camera.domain.model.DetectedFace

internal data class CameraStateUI(
    val name: String,
    val detectedRect: DetectedRect,
    val image: Bitmap?,
) {
    companion object {
        fun init(): CameraStateUI {
            return CameraStateUI(
                "",
                DetectedRect.Nothing,
                null,
            )
        }
    }
}

internal interface DetectedRect {
    data class Face(
        val xMin: Float,
        val xMax: Float,
        val yMin: Float,
        val yMax: Float,
    ) : DetectedRect

    data object Nothing : DetectedRect
}

internal fun DetectedFace?.toDetectedRect(): DetectedRect {
    if (this == null) return DetectedRect.Nothing
    return DetectedRect.Face(xMin, xMax, yMin, yMax)
}


