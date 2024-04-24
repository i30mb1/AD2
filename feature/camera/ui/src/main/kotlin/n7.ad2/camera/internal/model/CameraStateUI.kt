package n7.ad2.camera.internal.model

import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal data class CameraStateUI(
    val name: String = "",
    val detectedRect: DetectedRect = DetectedRect.Nothing,
    val image: Bitmap? = null,
    val timeoutForRecording: Duration = 4.seconds,

    // информация об экране для правильного преобразования фото для превью
    val scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FIT_CENTER,
    val viewWidth: Int = 0,
    val viewHeight: Int = 0,
)

internal interface DetectedRect {
    data class Face(
        val xMin: Float,
        val xMax: Float,
        val yMin: Float,
        val yMax: Float,
    ) : DetectedRect

    data object Nothing : DetectedRect
}
