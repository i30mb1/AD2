package n7.ad2.feature.camera.domain.impl

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import n7.ad2.feature.camera.domain.CameraSettings

class CameraProvider(
    private val context: Context,
    private val cameraSettings: CameraSettings,
    private val lifecycle: LifecycleOwner,
) {

    private val camera: ProcessCameraProvider by lazy {
// настройки камеры
//        ProcessCameraProvider.configureInstance(
//                CameraXConfig.Builder()
//                    .setCameraOpenRetryMaxTimeoutInMillisWhileResuming(1000)
//                    .build()
//            )
//        ProcessCameraProvider.awaitInstance(context)
        ProcessCameraProvider.getInstance(context).get()
    }

    @SuppressLint("RestrictedApi")
    fun bind(useCases: UseCaseGroup) {
        val cameraInfo: CameraInfo = camera.getCameraInfo(CameraSelector.DEFAULT_BACK_CAMERA)
        val camera: Camera = camera.bindToLifecycle(lifecycle, cameraSettings.cameraSelector(), useCases)
        camera.cameraControl

    }

    /**
     * Возможно анбайнд не нужен, юзкейсы сами отпишутся при onDestory
     * максимум чтобы прекращать работу в onPause, но смысла невижу
     */
    fun unbind() {
        camera.unbindAll()
    }
}
