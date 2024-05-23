package n7.ad2.feature.camera.domain.impl.recorder

import android.content.Context
import android.util.Range
import androidx.camera.core.MirrorMode
import androidx.camera.video.FileOutputOptions
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
import kotlinx.coroutines.Dispatchers
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

    private val recorder by lazy {
        VideoRecorder.Builder()
//            .setQualitySelector(
//                QualitySelector.from(
//                    Quality.HD,
//                    FallbackStrategy.lowerQualityOrHigherThan(Quality.HD),
//                )
//            )
//            .setAspectRatio(settings.aspectRatio)
            .build()
    }
    private val videoCapture: VideoCapture<VideoRecorder> by lazy {
        VideoCapture.withOutput(recorder)
        VideoCapture.Builder(recorder)
            .setVideoStabilizationEnabled(true)
            .setMirrorMode(MirrorMode.MIRROR_MODE_ON_FRONT_ONLY)
            .setTargetFrameRate(Range.create(15, 15))
            .build()
    }
    private var activeRecording: Recording? = null

    override suspend fun init() {

    }

    override suspend fun start(): File = withContext(dispatcher.IO) {
        withContext(Dispatchers.Main) { cameraProvider.bind(videoCapture) }


        suspendCoroutine { continuation: Continuation<File> ->
            val folder = File(context.filesDir, "temp")
            if (folder.exists().not()) {
                folder.mkdir()
            }
            val file = File(folder, "CameraX-recording.mp4")
            file.deleteOnExit()

            val fileOutputOption = FileOutputOptions.Builder(file)
                .setDurationLimitMillis(2.seconds.inWholeMilliseconds)
                .build()

            activeRecording = videoCapture!!.output
                .prepareRecording(context, fileOutputOption)
                .start(context.mainExecutor) startRecording@{ event: VideoRecordEvent ->
                    when (event) {
                        is VideoRecordEvent.Start -> {
                            println("start")
                        }

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
//            Thread.sleep(2_000)
//            activeRecording?.stop()

        }
    }

    override suspend fun stop() {
        activeRecording?.stop()
        activeRecording = null
    }
}
