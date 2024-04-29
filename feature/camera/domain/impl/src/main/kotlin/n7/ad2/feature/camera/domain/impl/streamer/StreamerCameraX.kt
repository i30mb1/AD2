package n7.ad2.feature.camera.domain.impl.streamer

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import n7.ad2.feature.camera.domain.CameraSettings
import n7.ad2.feature.camera.domain.Streamer
import n7.ad2.feature.camera.domain.impl.CameraProvider
import n7.ad2.feature.camera.domain.model.Image
import n7.ad2.feature.camera.domain.model.ImageMetadata
import org.jetbrains.kotlinx.dl.impl.preprocessing.camerax.toBitmap

@SuppressLint("RestrictedApi")
class StreamerCameraX(
    private val settings: CameraSettings,
    private val cameraProvider: CameraProvider,
    lifecycle: LifecycleOwner,
) : Streamer {

    private val counter = Counter()
    private val executor = Executors.newSingleThreadExecutor()
    private val imageAnalysis: ImageAnalysis by lazy {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .setResolutionSelector(
                ResolutionSelector.Builder()
                    .setAspectRatioStrategy(AspectRatioStrategy(settings.aspectRatio, AspectRatioStrategy.FALLBACK_RULE_NONE))
                    .build()
            )
            .build()
    }
    private val _stream: SharedFlow<Image> = callbackFlow {
        imageAnalysis.setAnalyzer(executor) { image: ImageProxy ->
            val result: Bitmap = image.toBitmap(applyRotation = true)
            image.close()
            val metadata = ImageMetadata(result.width, result.height, !settings.isFrontCamera)
            trySend(Image(result, metadata))
            counter.count++
        }

        cameraProvider.bind(imageAnalysis)
        awaitClose {
            runBlocking {
                cameraProvider.unbind(imageAnalysis)
                imageAnalysis.clearAnalyzer()
            }
        }
    }
        .shareIn(lifecycle.lifecycleScope, SharingStarted.Lazily)

    override val stream: SharedFlow<Image> = _stream

    companion object {
        val SUBSCRIBE_DELAY = 3.seconds
    }
}

class Counter {
    var count = 0
    val scope = CoroutineScope(Job())

    init {
        scope.launch {
            while (true) {
                delay(1.seconds)
                Log.d("N7", "streamer speed: $count")
                count = 0
            }
        }
    }
}