package n7.ad2.feature.camera.domain.impl

import android.app.Application
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executors
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import n7.ad2.feature.camera.domain.CameraSettings
import n7.ad2.feature.camera.domain.Streamer
import n7.ad2.feature.camera.domain.model.Image

class StreamerCameraX(
    private val application: Application,
    private val cameraSettings: CameraSettings,
    private val lifecycle: LifecycleOwner,
) : Streamer {

    private val _stream = MutableSharedFlow<Image>(replay = 1)
    override val stream: SharedFlow<Image> = _stream.asSharedFlow()

    private val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .build()
        .also {
            it.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                runBlocking {
                    val result = image.toBitmap()
                    _stream.emit(Image(result))
                    image.close()
                }
            }
        }

    override suspend fun start() {
        application.getCamera().bindToLifecycle(lifecycle, cameraSettings.cameraSelector(), imageAnalysis)
    }
}
