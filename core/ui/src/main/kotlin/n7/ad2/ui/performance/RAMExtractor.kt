package n7.ad2.ui.performance

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider

/**
 * Информация об использовании оперативной памяти используемой для нашего процесса
 */
internal class RAMExtractor(
    private val dispatcher: DispatchersProvider,
) {

    suspend fun get(): Info {
        return withContext(dispatcher.Default) {
            val runtime = Runtime.getRuntime()
            val usedMemoryMB = (runtime.totalMemory() - runtime.freeMemory()) / BYTES_IN_MEGABYTE
            val maxMemoryMB = runtime.maxMemory() / BYTES_IN_MEGABYTE
            Info(usedMemoryMB, maxMemoryMB)
        }
    }

    class Info(val usedMemoryMB: Long, val maxMemoryMB: Long)

    companion object {
        const val BYTES_IN_MEGABYTE: Long = 1048576L
    }
}
