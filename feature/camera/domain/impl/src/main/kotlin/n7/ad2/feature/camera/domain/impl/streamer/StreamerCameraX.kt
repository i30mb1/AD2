package n7.ad2.feature.camera.domain.impl.streamer

import android.util.Size
import androidx.camera.core.AspectRatio
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.UseCase
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.shareIn
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.camera.domain.CameraAspectRatio
import n7.ad2.feature.camera.domain.CameraSettings
import n7.ad2.feature.camera.domain.Streamer
import n7.ad2.feature.camera.domain.StreamerResolution
import n7.ad2.feature.camera.domain.impl.FPSTimer
import n7.ad2.feature.camera.domain.model.Image
import n7.ad2.feature.camera.domain.model.ImageMetadata
import n7.ad2.feature.camera.domain.model.StreamerState
import kotlin.time.Duration.Companion.seconds

class StreamerCameraX(private val settings: CameraSettings, dispatchers: DispatchersProvider, lifecycle: LifecycleOwner, private val fps: FPSTimer) : Streamer {

    private val dispatcher = dispatchers.Default.limitedParallelism(1).asExecutor()
    private val streamerResolution: MutableSet<StreamerResolution> = mutableSetOf()
    private val _stream = MutableSharedFlow<StreamerState>(0, 1, BufferOverflow.DROP_OLDEST)

    private val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageRotationEnabled(true)
        .setResolutionSelector(
            ResolutionSelector.Builder()
                .setResolutionFilter { sizes: MutableList<Size>, _ ->
                    sizes.sortedBy { size ->
                        // добавляем все возможные разрешения камеры
                        streamerResolution.add(StreamerResolution(size.width, size.height))
                        size.height * size.width
                    }
                }
                .setAspectRatioStrategy(
                    AspectRatioStrategy(
                        when (settings.aspectRatio) {
                            CameraAspectRatio.RATIO_16_9 -> AspectRatio.RATIO_16_9
                            CameraAspectRatio.RATIO_4_3 -> AspectRatio.RATIO_4_3
                        },
                        AspectRatioStrategy.FALLBACK_RULE_AUTO,
                    ),
                )
                .build(),
        )
        .build()

    override val stream: SharedFlow<StreamerState> = _stream
        .onCompletion { imageAnalysis.clearAnalyzer() }
        .shareIn(lifecycle.lifecycleScope, SharingStarted.WhileSubscribed(SUBSCRIBE_DELAY))

    override fun start(): UseCase {
        imageAnalysis
            .setAnalyzer(dispatcher) { image: ImageProxy ->
                val result = image.toBitmap()
                val metadata = ImageMetadata(result.width, result.height, !settings.isFrontCamera)
                _stream.tryEmit(StreamerState(Image(result, metadata), fps.counter.streamer))
                image.close()
                fps.counter.streamer++
            }
        return imageAnalysis
    }

    companion object {
        val SUBSCRIBE_DELAY = 3.seconds.inWholeMilliseconds
    }
}
