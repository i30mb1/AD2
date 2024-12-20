package n7.ad2.feature.camera.domain.impl

import android.content.Context
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import n7.ad2.app.logger.Logger
import n7.ad2.feature.camera.domain.CameraSettings

class CameraProvider(
    private val context: Context,
    private val cameraSettings: CameraSettings,
    private val lifecycle: LifecycleOwner,
    private val logger: Logger,
) {

    private val camera: ProcessCameraProvider by lazy {
// настройки камеры
//        ProcessCameraProvider.configureInstance(
//                CameraXConfig.Builder()
//                    .setCameraOpenRetryMaxTimeoutInMillisWhileResuming(1000)
//                    .build()
//            )
        // альтернативный способ получения ProcessCameraProvider
//        ProcessCameraProvider.awaitInstance(context)
        ProcessCameraProvider.getInstance(context).get()
    }

    fun bind(useCases: UseCaseGroup) {
        camera.bindToLifecycle(lifecycle, cameraSettings.cameraSelector(), useCases)
        val cameraInfo = camera.getCameraInfo(cameraSettings.cameraSelector())
        logger.log("Camera supportedFrameRateRanges: ${cameraInfo.supportedFrameRateRanges}")
    }

    /**
     * Возможно анбайнд не нужен, юзкейсы сами отпишутся при onDestory
     * максимум чтобы прекращать работу в onPause, но смысла невижу
     */
    fun unbind() {
        camera.unbindAll()
    }
}
