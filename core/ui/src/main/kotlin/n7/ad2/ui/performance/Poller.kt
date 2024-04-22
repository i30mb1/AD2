package n7.ad2.ui.performance

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import java.util.LinkedList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.ktx.lazyUnsafe

interface Poller {
    /**
     * история нагрузки
     * в начале списка - старые, в конце - новые
     */
    val usage: SharedFlow<List<ResourceUsage>>
}

internal class PollerImpl(
    private val context: Context,
    private val lifecycle: Lifecycle,
) : Poller {

    private val _usage: MutableSharedFlow<List<ResourceUsage>> = MutableSharedFlow(replay = 1)
    override val usage: SharedFlow<List<ResourceUsage>> = _usage.asSharedFlow()

    private val extractor by lazyUnsafe {
        val dispatchers = DispatchersProvider()
        PerformanceExtractorImpl(
            context,
            UsageInfoMapper(),
            CPUExtractor(dispatchers),
            RAMExtractor(dispatchers),
            FpsExtractor(lifecycle),
        )
    }
    private var job: Job? = null

    init {
        start()
    }

    fun start() {
        job?.cancel()
        job = lifecycle.coroutineScope.launch {
            val list = LinkedList<ResourceUsage>()
            while (true) {
                val usage = extractor.get()
                if (list.size >= CACHE_CAPACITY) {
                    repeat(list.size - CACHE_CAPACITY + 1) {
                        list.removeFirst()
                    }
                }
                list.add(usage)
                _usage.emit(list)
                delay(POLLING_RATE_MS)
            }
        }
    }

    companion object {
        private const val POLLING_RATE_MS: Long = 300L
        private const val CACHE_CAPACITY: Int = 100
    }
}