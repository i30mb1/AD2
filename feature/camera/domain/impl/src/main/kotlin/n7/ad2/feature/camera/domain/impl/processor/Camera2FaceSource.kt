package n7.ad2.feature.camera.domain.impl.processor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.hardware.camera2.TotalCaptureResult
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.ExtendableBuilder
import n7.ad2.feature.camera.domain.CameraSettings
import n7.ad2.feature.camera.domain.model.DetectedFaceNormalized

@SuppressLint("UnsafeOptInUsageError")
class Camera2FaceSource(context: Context, cameraSettings: CameraSettings) {

    @Volatile
    private var latest: DetectedFaceNormalized? = null

    private val supportedMode: Int? = resolveSupportedMode(context, cameraSettings)

    private val callback = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
            val faces = result.get(CaptureResult.STATISTICS_FACES)
            val crop = result.get(CaptureResult.SCALER_CROP_REGION)
            val best = faces?.maxByOrNull { it.score }
            latest = if (best == null || crop == null) null else normalize(best.bounds, crop, best.score)
        }
    }

    fun face(): DetectedFaceNormalized? = latest

    @ExperimentalCamera2Interop
    fun <T> attach(builder: ExtendableBuilder<T>) {
        val mode = supportedMode ?: return
        Camera2Interop.Extender(builder)
            .setCaptureRequestOption(CaptureRequest.STATISTICS_FACE_DETECT_MODE, mode)
            .setSessionCaptureCallback(callback)
    }

    private fun normalize(bounds: Rect, crop: Rect, score: Int): DetectedFaceNormalized {
        val width = crop.width().toFloat()
        val height = crop.height().toFloat()
        return DetectedFaceNormalized(
            ((bounds.left - crop.left) / width).coerceIn(0f, 1f),
            ((bounds.right - crop.left) / width).coerceIn(0f, 1f),
            ((bounds.top - crop.top) / height).coerceIn(0f, 1f),
            ((bounds.bottom - crop.top) / height).coerceIn(0f, 1f),
            score / 100f,
        )
    }

    private fun resolveSupportedMode(context: Context, cameraSettings: CameraSettings): Int? = try {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val lensFacing = if (cameraSettings.isFrontCamera) CameraMetadata.LENS_FACING_FRONT else CameraMetadata.LENS_FACING_BACK
        val cameraId = manager.cameraIdList.firstOrNull { id ->
            manager.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING) == lensFacing
        }
        val modes = cameraId
            ?.let { manager.getCameraCharacteristics(it).get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES) }
            ?: intArrayOf()
        when {
            modes.contains(CameraMetadata.STATISTICS_FACE_DETECT_MODE_FULL) -> CameraMetadata.STATISTICS_FACE_DETECT_MODE_FULL
            modes.contains(CameraMetadata.STATISTICS_FACE_DETECT_MODE_SIMPLE) -> CameraMetadata.STATISTICS_FACE_DETECT_MODE_SIMPLE
            else -> null
        }
    } catch (error: Exception) {
        null
    }
}
