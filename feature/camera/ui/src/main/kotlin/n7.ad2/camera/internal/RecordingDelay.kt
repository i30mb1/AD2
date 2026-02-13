package n7.ad2.camera.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import n7.ad2.coroutines.DispatchersProvider
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

interface RecordingDelay {
    val state: MutableStateFlow<Duration>
    fun startOnce(scope: CoroutineScope)
    fun stop()
}

internal class RecordingDelayImpl(private val dispatcher: DispatchersProvider, private val initValue: Duration = 5.seconds, private val step: Duration = 100.milliseconds) : RecordingDelay {

    override val state: MutableStateFlow<Duration> = MutableStateFlow(initValue)
    private var job: Job? = null

    override fun startOnce(scope: CoroutineScope) {
        if (job != null) return
        job = scope.launch(dispatcher.IO) {
            while (state.value.isPositive()) {
                delay(step)
                state.update { curr -> curr.minus(step) }
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
