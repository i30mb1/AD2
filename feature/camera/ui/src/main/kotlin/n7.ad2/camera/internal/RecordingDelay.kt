package n7.ad2.camera.internal

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import n7.ad2.coroutines.DispatchersProvider

interface RecordingDelay {
    val state: MutableStateFlow<Duration>
    fun startOnce(scope: CoroutineScope)
    fun stop()
}

internal class RecordingDelayImpl(
    private val dispatcher: DispatchersProvider,
    private val initValue: Duration = 5.seconds,
) : RecordingDelay {

    override val state: MutableStateFlow<Duration> = MutableStateFlow(initValue)
    private var job: Job? = null

    override fun startOnce(scope: CoroutineScope) {
        if (job != null) return
        job = scope.launch(dispatcher.IO) {
            while (state.value.isPositive()) {
                delay(1.seconds)
                state.update { curr -> curr.minus(1.seconds) }
            }
            job = null
        }
    }

    override fun stop() {
        job?.cancel()
        job = null
        state.update { initValue }
    }
}
