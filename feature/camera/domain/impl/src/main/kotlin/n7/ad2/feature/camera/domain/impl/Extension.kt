package n7.ad2.feature.camera.domain.impl

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun Context.getCamera(): ProcessCameraProvider = suspendCoroutine { continuation ->
    val future = ProcessCameraProvider.getInstance(this)
    future.addListener(
        { continuation.resume(future.get()) },
        ContextCompat.getMainExecutor(this)
    )
}
