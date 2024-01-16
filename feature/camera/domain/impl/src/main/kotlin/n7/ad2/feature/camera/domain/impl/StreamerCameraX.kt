package n7.ad2.feature.camera.domain.impl

import android.app.Application
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import n7.ad2.feature.camera.domain.CameraSettings
import n7.ad2.feature.camera.domain.Streamer
import n7.ad2.feature.camera.domain.model.Image
import org.jetbrains.kotlinx.dl.impl.preprocessing.camerax.toBitmap

class StreamerCameraX(
    private val application: Application,
    private val cameraSettings: CameraSettings,
    private val lifecycle: LifecycleOwner,
) : Streamer {

    private val imageAnalysis by lazy {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
    }
    private val _stream: SharedFlow<Image> = callbackFlow {
        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
            val result = image.toBitmap(applyRotation = true)
            trySend(Image(result))
            image.close()
        }
        application.getCamera().bindToLifecycle(lifecycle, cameraSettings.cameraSelector(), imageAnalysis)
        awaitClose {
            imageAnalysis.clearAnalyzer()
        }
    }
        .shareIn(lifecycle.lifecycleScope, SharingStarted.WhileSubscribed(5.seconds))

    override val stream: SharedFlow<Image> = _stream
}
