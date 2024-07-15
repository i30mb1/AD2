package n7.ad2.feature.camera.domain

import java.io.File
import kotlin.time.Duration
import kotlinx.coroutines.flow.Flow

interface Recorder {
    val state: Flow<RecorderState>
    suspend fun init(): Any
    suspend fun startOnce()
    suspend fun stop()
}

sealed interface RecorderState {
    data object Idle : RecorderState
    class Canceled(val error: String) : RecorderState
    class Started(val time: Duration) : RecorderState
    class Completed(val file: File) : RecorderState
    class Error(val error: Throwable) : RecorderState
}
