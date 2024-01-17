package n7.ad2.feature.camera.domain.impl

import android.content.Context
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import n7.ad2.feature.camera.domain.CameraSettings

class CameraProvider(
    private val context: Context,
    private val cameraSettings: CameraSettings,
    private val lifecycle: LifecycleOwner,
) {

    suspend fun bind(useCase: UseCase) {
        getCamera().bindToLifecycle(lifecycle, cameraSettings.cameraSelector(), useCase)
    }

    suspend fun unbind(useCase: UseCase) {
        getCamera().unbind(useCase)
    }

    suspend fun getCamera(): ProcessCameraProvider = suspendCoroutine { continuation ->
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener(
            { continuation.resume(future.get()) },
            ContextCompat.getMainExecutor(context)
        )
    }
}