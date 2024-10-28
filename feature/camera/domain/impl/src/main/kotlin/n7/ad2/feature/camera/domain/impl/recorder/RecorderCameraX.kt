package n7.ad2.feature.camera.domain.impl.recorder

import android.content.Context
import androidx.camera.core.UseCase
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.video.VideoRecordEvent.Finalize
import androidx.camera.video.VideoRecordEvent.Finalize.ERROR_DURATION_LIMIT_REACHED
import androidx.camera.video.VideoRecordEvent.Finalize.ERROR_ENCODING_FAILED
import androidx.camera.video.VideoRecordEvent.Finalize.ERROR_FILE_SIZE_LIMIT_REACHED
import androidx.camera.video.VideoRecordEvent.Finalize.ERROR_INSUFFICIENT_STORAGE
import androidx.camera.video.VideoRecordEvent.Finalize.ERROR_INVALID_OUTPUT_OPTIONS
import androidx.camera.video.VideoRecordEvent.Finalize.ERROR_NONE
import androidx.camera.video.VideoRecordEvent.Finalize.ERROR_NO_VALID_DATA
import androidx.camera.video.VideoRecordEvent.Finalize.ERROR_RECORDER_ERROR
import androidx.camera.video.VideoRecordEvent.Finalize.ERROR_RECORDING_GARBAGE_COLLECTED
import androidx.camera.video.VideoRecordEvent.Finalize.ERROR_SOURCE_INACTIVE
import androidx.camera.video.VideoRecordEvent.Finalize.ERROR_UNKNOWN
import androidx.camera.video.VideoRecordEvent.Finalize.VideoRecordError
import androidx.camera.video.VideoRecordEvent.Start
import androidx.camera.video.VideoRecordEvent.Status
import java.io.File
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.camera.domain.Recorder
import n7.ad2.feature.camera.domain.RecorderState
import androidx.camera.video.Recorder as VideoRecorder

class RecorderCameraX(
    private val context: Context,
    private val logger: Logger,
    private val dispatcher: DispatchersProvider,
) : Recorder {

    private val _state: MutableStateFlow<RecorderState> = MutableStateFlow(RecorderState.Idle)
    override val state: StateFlow<RecorderState> = _state.asStateFlow()

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
        VideoCapture.Builder(recorder)
//            .setVideoStabilizationEnabled(true)
//            .setMirrorMode(MirrorMode.MIRROR_MODE_ON_FRONT_ONLY)
//            .setTargetFrameRate(Range.create(15, 15))
            .build()
    }
    private var activeRecording: Recording? = null
    private var isCancelled: Boolean = false

    override suspend fun init(): UseCase {
        return videoCapture
    }

    override suspend fun startOnce(): Unit = withContext(dispatcher.IO) {
        if (activeRecording != null) return@withContext
        suspendCoroutine { continuation: Continuation<File> ->
            val folder = File(context.filesDir, "temp")
            folder.mkdir()
            val file = File(folder, "CameraX-recording.mp4")
            file.deleteOnExit()

            val recordingTime = 2.seconds.inWholeMilliseconds
            val fileOutputOption = FileOutputOptions.Builder(file)
                .setDurationLimitMillis(recordingTime)
                .build()

            activeRecording = videoCapture.output
                .prepareRecording(context, fileOutputOption)
                .start(context.mainExecutor) startRecording@{ event: VideoRecordEvent ->
                    when (event) {
                        is Start -> {
                            isCancelled = false
                            _state.tryEmit(RecorderState.Started(0.seconds))
                        }

                        is Status -> {
                            val sec: Long = event.recordingStats.recordedDurationNanos + 1
                            _state.tryEmit(RecorderState.Started(sec.nanoseconds))
                        }

                        is Finalize -> {
                            when (event.error) {
                                // видео записалось благодаря установленому лимиту setDurationLimitMillis
                                ERROR_DURATION_LIMIT_REACHED -> {
                                    _state.tryEmit(RecorderState.Completed(file))
                                }

                                else -> {
                                    val error = errorToString(event.error)
                                    if (isCancelled) {
                                        _state.tryEmit(RecorderState.Canceled(error))
                                    } else {
                                        val message = "cause - ${event.cause}, error - $error"
                                        val state = RecorderState.Error(Exception(message))
                                        _state.tryEmit(state)
                                    }
                                }
                            }
                            activeRecording = null
                        }
                    }
                }
        }
    }

    private fun errorToString(@VideoRecordError error: Int): String {
        when (error) {
            ERROR_NONE -> return "ERROR_NONE"
            ERROR_UNKNOWN -> return "ERROR_UNKNOWN"
            ERROR_FILE_SIZE_LIMIT_REACHED -> return "ERROR_FILE_SIZE_LIMIT_REACHED"
            ERROR_INSUFFICIENT_STORAGE -> return "ERROR_INSUFFICIENT_STORAGE"
            ERROR_INVALID_OUTPUT_OPTIONS -> return "ERROR_INVALID_OUTPUT_OPTIONS"
            ERROR_ENCODING_FAILED -> return "ERROR_ENCODING_FAILED"
            ERROR_RECORDER_ERROR -> return "ERROR_RECORDER_ERROR"
            ERROR_NO_VALID_DATA -> return "ERROR_NO_VALID_DATA"
            ERROR_SOURCE_INACTIVE -> return "ERROR_SOURCE_INACTIVE"
            ERROR_DURATION_LIMIT_REACHED -> return "ERROR_DURATION_LIMIT_REACHED"
            ERROR_RECORDING_GARBAGE_COLLECTED -> return "ERROR_RECORDING_GARBAGE_COLLECTED"
        }
        // Should never reach here, but just in case...
        return "Unknown($error)"
    }

    override suspend fun stop() {
        activeRecording?.stop()
        activeRecording = null
    }
}
