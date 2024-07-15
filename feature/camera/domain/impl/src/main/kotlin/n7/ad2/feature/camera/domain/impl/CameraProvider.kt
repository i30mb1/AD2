package n7.ad2.feature.camera.domain.impl

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import n7.ad2.feature.camera.domain.CameraSettings

class CameraProvider(
    private val context: Context,
    private val cameraSettings: CameraSettings,
    private val lifecycle: LifecycleOwner,
) {

    private val camera by lazy {
        ProcessCameraProvider.getInstance(context).get()
    }

    @SuppressLint("RestrictedApi")
    fun bind(useCases: UseCaseGroup) {
        val camera = camera.bindToLifecycle(lifecycle, cameraSettings.cameraSelector(), useCases)
//        val config = CameraXConfig.Builder()
//            .setCameraOpenRetryMaxTimeoutInMillisWhileResuming(1000)
//            .build()
//
//        ProcessCameraProvider.configureInstance(config)
    }

    /**
     * Возможно анбайнд не нужен, юзкейсы сами отпишутся при onDestory
     * максимум чтобы прекращать работу в onPause, но смысла невижу
     */
    fun unbind() {
        camera.unbindAll()
    }
}
