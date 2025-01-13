package n7.ad2.feature.camera.domain.impl.processor

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.hardware.camera2.TotalCaptureResult
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.CaptureRequestOptions
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraInfo
import androidx.camera.lifecycle.ProcessCameraProvider
import n7.ad2.feature.camera.domain.CameraSettings
import n7.ad2.feature.camera.domain.Processor
import n7.ad2.feature.camera.domain.impl.cameraSelector
import n7.ad2.feature.camera.domain.model.Image
import n7.ad2.feature.camera.domain.model.ProcessorState

@SuppressLint("UnsafeOptInUsageError")
class ProcessorCamera2(
    private val context: Context,
    private val cameraSettings: CameraSettings,
) : Processor {

    val callback = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
            val faces = result.get(CaptureResult.STATISTICS_FACES)
        }
    }

    @ExperimentalCamera2Interop
    override fun analyze(image: Image): ProcessorState {
        val camera: ProcessCameraProvider = ProcessCameraProvider.getInstance(context).get()
        val cameraInfo: Camera2CameraInfo = cameraSettings.cameraSelector()
            .filter(camera.availableCameraInfos)
            .first()
            .let { Camera2CameraInfo.from(it) }
        val level = cameraInfo.getCameraCharacteristic(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
        val faceDetectMode = cameraInfo.getCameraCharacteristic(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES) ?: intArrayOf()
        if (faceDetectMode.contains(CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_OFF)) error("Device do not support face detect")

        // отслеживать расширения для камеры
        // ExtensionsManager.getInstanceAsync(context, camera).get()
        // новое апи для получени инфы вместо получения инфы во время bindToLifecycle камеры
        val cameraInfo2: CameraInfo = camera.getCameraInfo(cameraSettings.cameraSelector())

        // включаем фичу нахождения лица
        CaptureRequestOptions.Builder()
            .setCaptureRequestOption(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CaptureRequest.STATISTICS_FACE_DETECT_MODE_SIMPLE)
            .build()
        // либо через usecase
//        Camera2Interop.Extender(usecase).setCaptureRequestOption(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CaptureRequest.STATISTICS_FACE_DETECT_MODE_SIMPLE)
        // устанавливаем коллбек
//        Camera2Interop.Extender(camera).setSessionCaptureCallback(callback)
        return TODO()
    }

    private fun mapCameraHardwareLevel(level: Int): String {
        return when (level) {
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY -> "INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY"
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL -> "INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL"
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED -> "INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED"
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL -> "INFO_SUPPORTED_HARDWARE_LEVEL_FULL"
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3 -> "INFO_SUPPORTED_HARDWARE_LEVEL_3"
            else -> "Unknown level :$level"
        }
    }
}
