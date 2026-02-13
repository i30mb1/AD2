package n7.ad2.ui.performance

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.ktx.lazyUnsafe
import java.io.File
import java.io.RandomAccessFile
import java.util.regex.Pattern

/**
 * Информация о загруженности процессора для всего Андройда
 */
internal class CPUExtractor(private val dispatchers: DispatchersProvider) {

    private val coresFreq: Array<CoreFreq> by lazyUnsafe { Array(size = getCores()) { CoreFreq(it) } }
    private val cpuPatter: Pattern by lazyUnsafe { Pattern.compile("cpu[0-9]+") }

    suspend fun get(): Int = withContext(dispatchers.IO) {
        coresFreq.sumOf { it.getCurUsage() } / coresFreq.size
    }

    private fun getCores(): Int = runCatching {
        File("/sys/devices/system/cpu/")
            .listFiles { pathname -> cpuPatter.matcher(pathname.name).matches() }!!
            .size
    }
//            .onFailure { error -> log }
        .mapCatching { Runtime.getRuntime().availableProcessors() }
//            .onFailure { error -> log }
        .getOrElse { 1 }

    internal class CoreFreq(private val coreIndex: Int) {

        private var cur = 0
        private var min = 0
        private var max = 0

        init {
            min = getCpuFreq(FreqPath.MIN)
            max = getCpuFreq(FreqPath.MAX)
        }

        fun getCurUsage(): Int {
            updateCurFreq()
            var cpuUsage = 0
            if (max - min > 0 && max > 0 && cur > 9) {
                cpuUsage = (cur - min) * 100 / (max - min)
            }
            return cpuUsage
        }

        private fun updateCurFreq() {
            cur = getCpuFreq(FreqPath.CUR)
            if (min == 0) min = getCpuFreq(FreqPath.MIN)
            if (max == 0) max = getCpuFreq(FreqPath.MAX)
        }

        private fun getCpuFreq(freqPath: FreqPath): Int = runCatching {
            RandomAccessFile(freqPath.get(coreIndex), "r")
                .use { reader -> reader.readLine().toInt() }
        }
//                .onFailure { error -> "" }
            .getOrElse { 0 }

        private enum class FreqPath(private val end: String) {
            MIN("cpuinfo_min_freq"),
            MAX("cpuinfo_max_freq"),
            CUR("scaling_cur_freq"),
            ;

            fun get(coreIndex: Int): String = "/sys/devices/system/cpu/cpu$coreIndex/cpufreq/$end"
        }
    }
}
