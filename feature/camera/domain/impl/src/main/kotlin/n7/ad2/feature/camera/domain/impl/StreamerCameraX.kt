package n7.ad2.feature.camera.domain.impl

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
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
import kotlinx.coroutines.runBlocking
import n7.ad2.feature.camera.domain.Streamer
import n7.ad2.feature.camera.domain.model.Image
import org.jetbrains.kotlinx.dl.impl.preprocessing.camerax.toBitmap

/**
 * Сущность отвечающая за раздачу кадров с камеры
 */
class StreamerCameraX(
    private val cameraProvider: CameraProvider,
    lifecycle: LifecycleOwner,
) : Streamer {

    private val imageAnalysis: ImageAnalysis by lazy {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
    }
    private val _stream: SharedFlow<Image> = callbackFlow {
        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { image: ImageProxy ->
            val result = image.toBitmap(applyRotation = true)
            trySend(Image(result))
            image.close()
        }

        cameraProvider.bind(imageAnalysis)
        awaitClose {
            runBlocking {
                cameraProvider.unbind(imageAnalysis)
                imageAnalysis.clearAnalyzer()
            }
        }
    }
        .shareIn(lifecycle.lifecycleScope, SharingStarted.WhileSubscribed(SUBSCRIBE_DELAY))

    override val stream: SharedFlow<Image> = _stream

    companion object {
        val SUBSCRIBE_DELAY = 3.seconds
    }
}
