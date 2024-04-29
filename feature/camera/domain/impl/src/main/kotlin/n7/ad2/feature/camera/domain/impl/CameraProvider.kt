package n7.ad2.feature.camera.domain.impl

import android.annotation.SuppressLint
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

    @SuppressLint("RestrictedApi")
    fun bind(useCase: UseCase) {
        val camera = camera.bindToLifecycle(lifecycle, cameraSettings.cameraSelector(), useCase)

//        UseCaseGroup.Builder()
//            .addUseCase(useCase)
//            .build()
    }

    fun unbind(useCase: UseCase) {
        camera.unbind(useCase)
    }
}