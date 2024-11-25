package n7.ad2.feature.camera.domain

import java.io.File
import kotlin.time.Duration
import kotlinx.coroutines.flow.Flow

interface Recorder {
    val state: Flow<RecorderState>
    fun init(): Any
    fun startOnce()
    fun stop()
}

sealed interface RecorderState {
    data object Idle : RecorderState
    data class Canceled(val error: String) : RecorderState
    data class Started(val time: Duration) : RecorderState
    data class Completed(val file: File) : RecorderState
    data class Error(val error: Throwable) : RecorderState
}
