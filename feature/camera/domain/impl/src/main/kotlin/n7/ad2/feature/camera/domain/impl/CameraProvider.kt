package n7.ad2.feature.camera.domain.impl

import android.content.Context
import androidx.camera.core.UseCase
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

    fun bind(useCase: UseCase) {
        camera.bindToLifecycle(lifecycle, cameraSettings.cameraSelector(), useCase)
    }

    fun unbind(useCase: UseCase) {
        camera.unbind(useCase)
    }
}