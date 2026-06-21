package n7.ad2.camera.internal.model

import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import n7.ad2.android.OneShotValue
import n7.ad2.app.logger.Logger
import n7.ad2.feature.camera.domain.FaceDetector
import n7.ad2.feature.camera.domain.Rotation
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal data class CameraStateUI(
    val name: String = "",
    val detectedRect: DetectedRect = DetectedRect.Nothing,
    val image: Bitmap? = null,
    val timeoutForRecording: Duration = 4.seconds,
    val isRecording: Boolean = false,
    val recordedFile: OneShotValue<File>? = null,
    val streamerFps: String = "0",
    val detectors: List<FaceDetector> = emptyList(),
    val currentDetector: FaceDetector? = null,
    val brightness: Float = 0f,
    val rotation: Rotation? = null,
    val occlusion: Float = 0f,
    val blurriness: Float = 0f,

    // информация об экране для правильного преобразования фото для превью
    val scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FIT_CENTER,
    val viewWidth: Int = 0,
    val viewHeight: Int = 0,
    val logger: Logger? = null,
)

internal interface DetectedRect {
    data class Face(val xMin: Float, val xMax: Float, val yMin: Float, val yMax: Float) : DetectedRect

    data object Nothing : DetectedRect
}
