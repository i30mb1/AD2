package n7.ad2.feature.camera.domain.impl.recorder

import android.content.Context
import androidx.camera.core.MirrorMode
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.video.VideoRecordEvent.Finalize
import java.io.File
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.camera.domain.CameraSettings
import n7.ad2.feature.camera.domain.Recorder
import n7.ad2.feature.camera.domain.impl.CameraProvider
import androidx.camera.video.Recorder as VideoRecorder

class RecorderCameraX(
    private val context: Context,
    private val settings: CameraSettings,
    private val cameraProvider: CameraProvider,
    private val dispatcher: DispatchersProvider,
) : Recorder {

    private var videoCapture: VideoCapture<VideoRecorder>? = null
    private var activeRecording: Recording? = null

    override suspend fun init() {
        val recorder = VideoRecorder.Builder()
            .setQualitySelector(
                QualitySelector.from(
                    Quality.HD,
                    FallbackStrategy.lowerQualityOrHigherThan(Quality.HD),
                )
            )
            .setAspectRatio(settings.aspectRatio)
            .build()
        videoCapture = VideoCapture.Builder(recorder)
            .setMirrorMode(MirrorMode.MIRROR_MODE_ON_FRONT_ONLY)
            .build()
        videoCapture?.let(cameraProvider::bind)
    }

    override suspend fun start(): File = withContext(dispatcher.IO) {
        suspendCoroutine { continuation: Continuation<File> ->
            val folder = File(context.filesDir, "temp")
            if (folder.exists().not()) folder.mkdir()
            val file = File(folder, "CameraX-recording.mp4")
            file.deleteOnExit()

            val fileOutputOption = FileOutputOptions.Builder(file)
                .setDurationLimitMillis(5.seconds.inWholeMilliseconds)
                .build()

            activeRecording = videoCapture!!.output
                .prepareRecording(context, fileOutputOption)
                .start(context.mainExecutor) startRecording@{ event: VideoRecordEvent ->
                    when (event) {
                        is Finalize -> {
                            if (isActive.not() || activeRecording == null) {
                                cancel()
                                return@startRecording
                            }
                            if (event.hasError() && event.error != Finalize.ERROR_DURATION_LIMIT_REACHED) {
                                file.delete()

                                if (event.error != Finalize.ERROR_SOURCE_INACTIVE) {
                                    continuation.resumeWithException(Exception(event.error.toString()))
                                }
                            } else {
                                continuation.resume(file)
                            }
                            activeRecording = null
                        }
                    }
                }
        }
    }

    override suspend fun stop() {
        activeRecording?.stop()
        activeRecording = null
    }
}
