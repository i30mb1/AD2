package n7.ad2.camera.internal

import android.app.Application
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking

class Streaming @Inject constructor(
    private val application: Application,
    private val lifecycle: CameraLifecycle,
) {

    private val _stream = MutableSharedFlow<Bitmap>(replay = 1)
    val stream = _stream.asSharedFlow()

    private val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .build()
        .also {
            it.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                runBlocking {
                    _stream.emit(image.toBitmap())
                    image.close()
                }

            }
        }

    suspend fun start() {
        application.getCamera().bindToLifecycle(lifecycle, settings.cameraSelector(), imageAnalysis)
    }
}
