package n7.ad2.feature.camera.domain.impl.streamer

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.UseCase
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.newSingleThreadContext
import n7.ad2.app.logger.Logger
import n7.ad2.feature.camera.domain.CameraSettings
import n7.ad2.feature.camera.domain.Streamer
import n7.ad2.feature.camera.domain.model.Image
import n7.ad2.feature.camera.domain.model.ImageMetadata
import n7.ad2.feature.camera.domain.model.StreamerState
import org.jetbrains.kotlinx.dl.impl.preprocessing.camerax.toBitmap

class StreamerCameraX(
    private val settings: CameraSettings,
    lifecycle: LifecycleOwner,
    logger: Logger,
) : Streamer {

    private val executor = newSingleThreadContext("streamer").executor
    private val _stream = MutableSharedFlow<StreamerState>(0, 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private var count = 0
    private var latestFps = 0
    private val timer: Flow<Unit> = ticker(1.seconds.inWholeMilliseconds)
        .consumeAsFlow()
        .onEach {
            logger.log("streamer speed: $count")
            latestFps = count
            count = 0
        }
    private val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setResolutionSelector(
            ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy(settings.aspectRatio, AspectRatioStrategy.FALLBACK_RULE_NONE))
                .build()
        )
        .build().apply {
            setAnalyzer(executor) { image: ImageProxy ->
                count++
                val result: Bitmap = image.toBitmap(applyRotation = true)
                val metadata = ImageMetadata(result.width, result.height, !settings.isFrontCamera)
                _stream.tryEmit(StreamerState(Image(result, metadata), latestFps))
                image.close()
            }
        }

    override val stream: SharedFlow<StreamerState> = _stream.combine(timer) { state, _ -> state }
        .shareIn(lifecycle.lifecycleScope, SharingStarted.WhileSubscribed(SUBSCRIBE_DELAY))

    override fun start(): UseCase {
        return imageAnalysis
    }

    companion object {
        val SUBSCRIBE_DELAY = 3.seconds.inWholeMilliseconds
    }
}
